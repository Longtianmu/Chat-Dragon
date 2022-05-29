package adaptors.mirai

import contact.Contacts
import contactListQQ
import contactsMap
import datas.RelationQQ
import datas.calculateRelationIDQQ
import groupListQQ
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.utils.BotConfiguration
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import relationQQ
import userQQBot
import java.io.File

class BotSets(qq: Long, password: String) {
    val userBot = BotFactory.newBot(qq, password) {
        cacheDir = File("cache/mirai")
        protocol = BotConfiguration.MiraiProtocol.MACOS
        redirectBotLogToDirectory(File("logs/mirai"))
        redirectNetworkLogToDirectory(File("logs/mirai"))
    }

    fun closeBot() {
        userBot.close()
    }
}

suspend fun initQQ(qqid: String, password: String) {
    userQQBot = BotSets(qqid.toLong(), password)
    userQQBot.userBot.login()
    contactListQQ.clear()
    groupListQQ.clear()
    contactsMap["QQ_Friend"] = mutableMapOf()
    contactsMap["QQ_Group"] = mutableMapOf()
    userQQBot.userBot.friends.forEach {
        contactListQQ.add(it)
        contactsMap["QQ_Friend"]!![it.id.toString()] =
            Contacts("QQ_Friend", it.id.toString(), it.nameCardOrNick, it.avatarUrl)
    }
    userQQBot.userBot.groups.forEach {
        groupListQQ.add(it)
        contactsMap["QQ_Group"]!![it.id.toString()] =
            Contacts("QQ_Group", it.id.toString(), it.name, it.avatarUrl)
    }
    suspendedTransactionAsync(Dispatchers.IO, db = relationQQ) {
        addLogger(StdOutSqlLogger)
        contactListQQ.forEach { friends ->
            RelationQQ.insert {
                it[relationID] = calculateRelationIDQQ(qqid.toLong(), friends.id.toString() + "QID")
                it[userID] = qqid.toLong()
                it[contactID] = friends.id.toString() + "QID"
            }
            commit()
        }
        groupListQQ.forEach { groups ->
            RelationQQ.insert {
                it[relationID] = calculateRelationIDQQ(qqid.toLong(), groups.id.toString() + "GID")
                it[userID] = qqid.toLong()
                it[contactID] = groups.id.toString() + "GID"
            }
            commit()
        }
        commit()
    }
    return
}
package com.example.plugins.email

import jakarta.activation.FileDataSource
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.LoggerFactory
import java.io.File

object EmailService {
    private val logger = LoggerFactory.getLogger("EamilService")
    private val mailPassword = System.getenv("EMAIL_PWD")

    fun sendEmail(path: String){
        val logFile = File(path)
        if(!logFile.exists()){
            logger.warn("## [EmailService] ${logFile.path} : Doesnt Exists. SKIP SEND EMAIL")
            return
        }

        val email = EmailBuilder.startingBlank()
            .from("jominkyu@gmail.com")
            .to("jominkyu@gmail.com")
            .withSubject("[${logFile.name} FDSearch LOG 기록]")
            .withPlainText("FDSearch LOG 기록")
            .withAttachment(File(path).name, FileDataSource(logFile))
            .buildEmail()

        val mailer = MailerBuilder
            .withSMTPServer("smtp.gmail.com", 25, "jominkyu@gmail.com", mailPassword)
            .buildMailer()

        mailer.sendMail(email)
        logger.warn("## [EmailService] ${logFile.path} : EMAIL SENT SUCCESS")
    }
}
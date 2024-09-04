package com.example.plugins.email

import com.example.DatabaseFactory
import com.example.LocalRepository
import jakarta.activation.FileDataSource
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.LoggerFactory
import java.io.File

object EmailService {
    private val logger = LoggerFactory.getLogger("EamilService")
    private val mailPassword = System.getenv("EMAIL_PWD")
    private val localRepository = LocalRepository(DatabaseFactory.dataSource)

    fun sendEmail(path: String){
        val logFile = File(path)
        if(!logFile.exists()){
            logger.warn("## [EmailService] ${logFile.path} : Doesnt Exists. SKIP SEND EMAIL")
            return
        }

        val email = EmailBuilder.startingBlank()
            .from("jominkyu@gmail.com")
            .to("jominkyu@gmail.com")
            .withSubject("## 로그파일 전송: ${logFile.name} ##")
            .withHTMLText(htmlTableText())
            .withAttachment(File(path).name, FileDataSource(logFile))
            .buildEmail()

        val mailer = MailerBuilder
            .withSMTPServer("smtp.gmail.com", 587, "jominkyu@gmail.com", mailPassword)
            .buildMailer()

        mailer.sendMail(email)
        localRepository.delteQueryCount() // 호출횟수 데이터 제거
        logger.warn("## [EmailService] ${logFile.path} : EMAIL SENT SUCCESS")
    }


    private fun htmlTableText(): String {
        val countMap = localRepository.getQueryCount()
        return MethodCounterDto.run {
            """
        <html>
        <head>
            <style>
                body {
                    font-family: Arial, sans-serif;
                }
                table {
                    width: 550px;
                    border-collapse: collapse;
                    margin: 20px 0;
                }
                th, td {
                    padding: 12px;
                    text-align: left;
                    border: 1px solid #ddd;
                }
                th {
                    background-color: #f2f2f2;
                    font-weight: bold;
                }
                tr:nth-child(even) {
                    background-color: #f9f9f9;
                }
                tr:hover {
                    background-color: #e9e9e9;
                }
                h2 {
                    color: #333;
                }
            </style>
        </head>
        <body>
            <h2>Endpoint별 요청수</h2>
            <table>
                <thead>
                    <tr>
                        <th>EndPoint</th>
                        <th>Count</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>/basic_info</td>
                        <td>${countMap["basic_info"] ?: -1}</td>
                    </tr>
                    <tr>
                        <td>/equipped_reactor</td>
                        <td>${countMap["equipped_reactor"] ?: -1}</td>
                    </tr>
                    <tr>
                        <td>/equipped_external</td>
                        <td>${countMap["equipped_external"] ?: -1}</td>
                    </tr>
                    <tr>
                        <td>/equipped_module</td>
                        <td>${countMap["equipped_module"] ?: -1}</td>
                    </tr>
                    <tr>
                        <td>/weapon_entity</td>
                        <td>${countMap["weapon_entity"] ?: -1}</td>
                    </tr>
                    <tr>
                        <td>others</td>
                        <td>${countMap["denied"] ?: -1}</td>
                    </tr>
                </tbody>
            </table>
        </body>
        </html>
        """.trimIndent()
        }
    }
}
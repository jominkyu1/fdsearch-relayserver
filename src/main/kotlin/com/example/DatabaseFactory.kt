package com.example

import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

object DatabaseFactory {
    lateinit var dataSource: DataSource

    fun init() {
        val sqliteDataSource = SQLiteDataSource()
        sqliteDataSource.url = "jdbc:sqlite:/home/ubuntu/fdsearch/fdsearch.db"
        //sqliteDataSource.url = "jdbc:sqlite:C:\\sqlite3\\fdsearch.db" //For test Local
        dataSource = sqliteDataSource
    }

    fun createTable(){
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                // Create tables

                // 칭호
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS TitleEntity (
                    title_id TEXT,
                    title_name String,
                    
                    PRIMARY KEY (title_id)
                )
                """.trimIndent())
                
                // 계승자
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Descendants (
                    descendant_id TEXT PRIMARY KEY,
                    descendant_name TEXT,
                    descendant_image_url TEXT
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS DescendantStatDetail (
                    descendant_id TEXT,
                    level INTEGER,
                    
                    stat_type TEXT,
                    stat_value Double,
                    
                    PRIMARY KEY (descendant_id, level, stat_type)
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS DescendantSkills (
                    descendant_id TEXT,
                    skill_type TEXT,
                    skill_name TEXT,
                    elment_type TEXT,
                    arche_type TEXT,
                    skill_image_url TEXT,
                    skill_description TEXT
                )
                """.trimIndent())

                // 모듈
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ModuleEntity (
                    module_Id TEXT,
                    image_Url TEXT,
                    module_Class TEXT,
                    module_Name TEXT,
                    module_SocketType TEXT,
                    module_Tier TEXT,
                    module_Type TEXT,
                    
                    PRIMARY KEY (module_Id)
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ModuleStatEntity (
                   module_Id TEXT, 
                   level INTEGER,
                   module_Capacity INTEGER,
                   value String,
                   
                   PRIMARY KEY (module_Id, level)
                )
                """.trimIndent())

                // 무기
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS WeaponEntity (
                   weapon_Id TEXT,
                   image_Url TEXT,
                   weapon_Name TEXT,
                   weapon_PerkAbilityDescription TEXT,
                   weapon_PerkAbilityImageUrl TEXT,
                   weapon_PerkAbilityName TEXT,
                   weapon_RoundsType TEXT,
                   weapon_Tier TEXT,
                   weapon_Type TEXT,
                   
                   PRIMARY KEY (weapon_Id)
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS WeaponBaseStatEntity (
                    weapon_Id TEXT,
                    stat_Id TEXT,
                    stat_Value Double,
                    
                    PRIMARY KEY (weapon_Id, stat_Id)    
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS WeaponFirearmEntity (
                   weapon_Id TEXT,
                   level Integer,
                   firearmAtkType TEXT,
                   firearmAtkValue Integer,
                   
                   PRIMARY KEY (weapon_Id, level, firearmAtkType)
                )
                """.trimIndent())

                // 스탯
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS StatEntity (
                   stat_id TEXT,
                   stat_name TEXT, 
                                     
                   PRIMARY KEY (stat_id)
                )
                """.trimIndent())

                //반응로
                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ReactorEntity(
                    reactor_id TEXT,
                
                    image_url TEXT,
                    optimized_condition_type TEXT,
                    reactor_name TEXT,
                    reactor_tier TEXT,
                
                    PRIMARY KEY (reactor_id)
                 )    
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ReactorSkillPowerEntity(
                    reactor_id TEXT,
                    level INT,
                    
                    skill_atk_power REAL,
                    sub_skill_atk_power REAL,
                    
                PRIMARY KEY (reactor_id, level)
)    
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ReactorEnchantEffectEntity(
                    reactor_id TEXT,
                    level INT,
                    enchant_level INT,

                    stat_type TEXT,
                    value REAL,

                    PRIMARY KEY (reactor_id, level, enchant_level)
                )
                """.trimIndent())

                stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ReactorSkillPowerCoefficientEntity(
                    reactor_id TEXT,
                    level INT,
                    coefficient_stat_id TEXT,

                    coefficient_stat_value REAL,

                    PRIMARY KEY (reactor_id, level, coefficient_stat_id)
                )
                """.trimIndent())
            }
        }
    }
}
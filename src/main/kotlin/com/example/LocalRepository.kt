package com.example

import com.example.data.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class LocalRepository(private val dataSource: DataSource) {
    private fun insertLog(name: String, size: Int = 0) {
        logger.info("Inserted $name to Local Server")
    }
    fun insertModuleMetaData(modules: List<ModuleOriginalData>){
        dataSource.connection.use { conn ->
            conn.autoCommit = false

            // ModuleEntity 테이블
            // module_Id, image_Url, module_Class, module_Name, module_SocketType, module_Tier, module_Type
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO ModuleEntity VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                modules.forEach { module ->
                    stmt.setString(1, module.moduleId)
                    stmt.setString(2, module.imageUrl)
                    stmt.setString(3, module.moduleClass)
                    stmt.setString(4, module.moduleName)
                    stmt.setString(5, module.moduleSocketType)
                    stmt.setString(6, module.moduleTier)
                    stmt.setString(7, module.moduleType)
                    stmt.addBatch()
                }

                stmt.executeBatch()
                conn.commit()
                insertLog("Modules", modules.size)
            }

            // ModuleStatEntity 테이블
            // module_id, level, module_capacity, value
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO ModuleStatEntity VALUES (?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                modules.flatMap { module ->
                    module.moduleStat.map { moduleStat ->
                        stmt.setString(1, module.moduleId)
                        stmt.setInt(2, moduleStat.level)
                        stmt.setInt(3, moduleStat.moduleCapacity)
                        stmt.setString(4, moduleStat.value)
                        stmt.addBatch()
                    }
                }

                stmt.executeBatch()
                conn.commit()
                insertLog("ModuleStat", modules.size)
            }
        }
    }

    fun insertDescendantsMetadata(descendants: List<DescendantOriginalData>) {
        //Descendants 테이블
        dataSource.connection.use { conn ->
            conn.autoCommit = false

            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO Descendants VALUES (?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                descendants.forEach { descendantOriginal ->
                    stmt.setString(1, descendantOriginal.descendant_id)
                    stmt.setString(2, descendantOriginal.descendant_name)
                    stmt.setString(3, descendantOriginal.descendant_image_url)
                    stmt.addBatch()
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Descendants", descendants.size)
            }

            //DescendantSkills 테이블
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO DescendantSkills VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                descendants.flatMap { entity ->
                    entity.descendant_skill.map { skillEntity ->
                        stmt.setString(1, entity.descendant_id)
                        stmt.setString(2, skillEntity.skill_type)
                        stmt.setString(3, skillEntity.skill_name)
                        stmt.setString(4, skillEntity.element_type)
                        stmt.setString(5, skillEntity.arche_type)
                        stmt.setString(6, skillEntity.skill_image_url)
                        stmt.setString(7, skillEntity.skill_description)
                        stmt.addBatch()
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Descendants Skill Entity (List Size) ", descendants.sumOf { it.descendant_skill.size })
            }

            //DescendantStatDetail 테이블
            //descendant_id, level, stat_type, stat_value
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO DescendantStatDetail VALUES (?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                descendants.flatMap { entity ->
                    entity.descendant_stat.flatMap { stat ->
                        stat.stat_detail.map { statDetail ->
                            stmt.setString(1, entity.descendant_id)
                            stmt.setInt(2, stat.level)
                            stmt.setString(3, statDetail.stat_type)
                            stmt.setDouble(4, statDetail.stat_value)
                            stmt.addBatch()
                        }
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Descendants Stat Detail Entity (List Size) ", descendants.sumOf { it.descendant_stat.size})
            }
        }
    }

    fun insertTitleMetadata(titles: List<TitleOriginalData>) {
        dataSource.connection.use { conn ->
            conn.autoCommit = false

            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO TitleEntity VALUES (?, ?)
                """.trimIndent()
            ).use {stmt ->
                titles.forEach { titleOriginal ->
                    stmt.setString(1, titleOriginal.title_id)
                    stmt.setString(2, titleOriginal.title_name)
                    stmt.addBatch()
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("TitleEntity", titles.size)
            }
        }
    }

    fun insertStatMetaData(stats: List<StatOriginalData>){
        dataSource.connection.use { conn ->
            conn.autoCommit = false
            
            // StatEntity 테이블
            // stat_id, stat_name
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO StatEntity VALUES (?, ?)
                """.trimIndent()
            ).use { stmt ->
                stats.forEach {stat ->
                    stmt.setString(1, stat.stat_id)
                    stmt.setString(2, stat.stat_name)
                    stmt.addBatch()
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Stat Entity", stats.size)
            }
        }
    }

    fun insertWeaponMetadata(weapons: List<WeaponOriginalData>){
        dataSource.connection.use { conn ->
            conn.autoCommit = false

            // WeaponEntity 테이블
            // weapon_Id, image_Url, weapon_Name, weapon_PerkAbilityDescription,
            // weapon_PerkAbilityImageUrl, weapon_PerkAbilityName, weapon_RoundsType,
            // weapon_Tier, weapon_Type
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO WeaponEntity VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                weapons.forEach { weapon ->
                    stmt.setString(1, weapon.weapon_id)
                    stmt.setString(2, weapon.image_url)
                    stmt.setString(3, weapon.weapon_name)
                    stmt.setString(4, weapon.weapon_perk_ability_description)
                    stmt.setString(5, weapon.weapon_perk_ability_image_url)
                    stmt.setString(6, weapon.weapon_perk_ability_name)
                    stmt.setString(7, weapon.weapon_rounds_type)
                    stmt.setString(8, weapon.weapon_tier)
                    stmt.setString(9, weapon.weapon_type)
                    stmt.addBatch()
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Weapon Entity", weapons.size)
            }

            // WeaponBaseStatEntity 테이블
            // weapon_id, stat_id, stat_value
            conn.prepareStatement(
                """
                    INSERT OR REPLACE INTO WeaponBaseStatEntity VALUES (?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                weapons.flatMap { weapon ->
                    weapon.base_stat.map { bStat ->
                        stmt.setString(1, weapon.weapon_id)
                        stmt.setString(2, bStat.stat_id)
                        stmt.setDouble(3, bStat.stat_value)
                        stmt.addBatch()
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Weapon BaseStat Entity", weapons.sumOf { it.base_stat.size })
            }

            // WeaponFirearmEntity 테이블
            // weapon_id, level, firearmatktype, firearmatkvalue
            val chunkValues = weapons.flatMap { weapon ->
                weapon.firearm_atk.flatMap { firearmAtk ->
                    firearmAtk.firearm.map { fireArm ->
                        Triple(
                            weapon.weapon_id, firearmAtk.level,
                            fireArm.firearm_atk_type to fireArm.firearm_atk_value
                        )
                    }
                }
            }

            // 10000건단위 인서트
            chunkValues.chunked(10000).forEach { chunk ->
                conn.prepareStatement(
                    """
                    INSERT OR REPLACE INTO WeaponFirearmEntity VALUES (?, ?, ?, ?)
                """.trimIndent()
                ).use { stmt ->
                    chunk.forEach { (weapon_id, level, firearm) ->
                        stmt.setString(1, weapon_id)
                        stmt.setInt(2, level)
                        stmt.setString(3, firearm.first)
                        stmt.setDouble(4, firearm.second)
                        stmt.addBatch()
                    }
                    stmt.executeBatch()
                    conn.commit()
                    insertLog("Weapon Firearm Entity (Chunk)", chunk.size)
                }
            }
        }
    }

    fun insertReactorMetaData(reactors: List<ReactorOriginalData>){
        dataSource.connection.use { conn ->
            conn.autoCommit = false

            //ReactorEntity
            //reactor_id, image_url, optimized_condition_type, reactor_name, reactor_tier
            conn.prepareStatement("""
                INSERT OR REPLACE INTO ReactorEntity VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            ).use { stmt ->
                reactors.map { reactor ->
                    stmt.setString(1, reactor.reactorId)
                    stmt.setString(2, reactor.imageUrl)
                    stmt.setString(3, reactor.optimizedConditionType)
                    stmt.setString(4, reactor.reactorName)
                    stmt.setString(5, reactor.reactorTier)
                    stmt.addBatch()
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("Reactor Entity", reactors.size)
            }

            //ReactorSkillPowerEntity
            //reactor_id TEXT, level , skill_atk_power , sub_skill_atk_power
            conn.prepareStatement("""
                INSERT OR REPLACE INTO ReactorSkillPowerEntity VALUES (?, ?, ?, ?)
            """.trimIndent()
            ).use { stmt ->
                reactors.flatMap { reactor ->
                    reactor.reactorSkillPower.map { reactorSkillPower ->
                        stmt.setString(1, reactor.reactorId)
                        stmt.setInt(2, reactorSkillPower.level)
                        stmt.setDouble(3, reactorSkillPower.skillAtkPower)
                        stmt.setDouble(4, reactorSkillPower.subSkillAtkPower)
                        stmt.addBatch()
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("ReactorSkillPower Entity")
            }


            //ReactorEnchantEffectEntity
            //reactor_id , level , enchant_level , stat_type , value ,
            conn.prepareStatement("""
                INSERT OR REPLACE INTO ReactorEnchantEffectEntity VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            ).use { stmt ->
                reactors.flatMap { reactor ->
                    reactor.reactorSkillPower.flatMap { reactorSp ->
                        reactorSp.enchantEffect.map { reactorEffect ->
                            stmt.setString(1, reactor.reactorId)
                            stmt.setInt(2, reactorSp.level)
                            stmt.setInt(3, reactorEffect.enchantLevel)
                            stmt.setString(4, reactorEffect.statType)
                            stmt.setDouble(5, reactorEffect.value)
                            stmt.addBatch()
                        }
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("ReactorEnchantEffect Entity")
            }
            //ReactorSkillPowerCoefficientEntity
            // reactor_id , level , coefficient_stat_id , coefficient_stat_value ,
            conn.prepareStatement("""
                INSERT OR REPLACE INTO ReactorSkillPowerCoefficientEntity VALUES (?, ?, ?, ?)
            """.trimIndent()
            ).use { stmt ->
                reactors.flatMap { reactor ->
                    reactor.reactorSkillPower.flatMap { reactorSp ->
                        reactorSp.skillPowerCoefficient.map { reactorPC ->
                            stmt.setString(1, reactor.reactorId)
                            stmt.setInt(2, reactorSp.level)
                            stmt.setString(3, reactorPC.coefficientStatId)
                            stmt.setDouble(4, reactorPC.coefficientStatValue)
                            stmt.addBatch()
                        }
                    }
                }
                stmt.executeBatch()
                conn.commit()
                insertLog("ReactorSkillPowerCoefficient Entity")
            }
        }
    }
}
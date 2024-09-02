package com.example

import com.example.dto.*
import javax.sql.DataSource

class LocalRepositoryToApp(private val dataSource: DataSource) {
    fun getCloudBasicInfo(
        descendant_id: String = "NOTHING",
        title_prefix_id: String = "NOTHING",
        title_suffix_id: String = "NOTHING",
    ): CloudBasicInfo{
        dataSource.connection.use { conn ->
            conn.prepareStatement(
                """
                    SELECT
                        descendant_name, 
                        descendant_image_url, 
                    ( SELECT IFNULL(title_name, '') FROM TitleEntity where title_id = ?) as title_prefix_name, 
                    ( SELECT IFNULL(title_name, '') FROM TitleEntity where title_id = ?) as title_suffix_name 
                    FROM Descendants 
                    WHERE descendant_id = ? 
                """.trimIndent()
            ).use {stmt ->
                stmt.setString(1, title_prefix_id)
                stmt.setString(2, title_suffix_id)
                stmt.setString(3, descendant_id)

                val resultSet = stmt.executeQuery()
                if(resultSet.next()){
                    return CloudBasicInfo(
                        title_prefix_name = resultSet.getString("title_prefix_name") ?: "",
                        title_suffix_name = resultSet.getString("title_suffix_name") ?: "",
                        descendant_name = resultSet.getString("descendant_name") ?: "",
                        descendant_image_url = resultSet.getString("descendant_image_url") ?: ""
                    )
                }
            }
        }

        // 못찾았을경우
        return CloudBasicInfo()
    }

    fun getEquippedModuleByIdLevel(modules: List<Module>): List<EquippedModule> {
        if(modules.isEmpty()) return emptyList()

        val whereConditions = modules.joinToString(" OR ") {
            "(MS.module_id = ${it.module_id} AND MS.level = ${it.module_enchant_level})"
        }
        val query =
            """
            SELECT 
            M.Module_Name, M.module_Class, M.module_SocketType, M.module_Type, M.module_Tier, 
            M.image_Url, MS.module_Id, MS.level, module_Capacity, value 
            FROM ModuleEntity M 
            INNER JOIN ModuleStatEntity MS ON M.module_id = MS.module_Id 
            WHERE ${whereConditions}
            """.trimIndent()
        val equippedModule = mutableListOf<EquippedModule>()

        dataSource.connection.use {conn ->
            conn.prepareStatement(query).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while(rs.next()){
                        val eqModule = EquippedModule(
                            moduleName = rs.getString("Module_Name"),
                            moduleClass = rs.getString("module_Class"),
                            moduleSocketType = rs.getString("module_SocketType"),
                            moduleType = rs.getString("module_Type") ?: "",
                            moduleTier = rs.getString("module_Tier"),
                            imageUrl = rs.getString("image_Url"),
                            moduleId = rs.getString("module_Id"),
                            level = rs.getInt("level"),
                            moduleCapacity = rs.getInt("module_Capacity"),
                            value = rs.getString("value")
                        )
                        equippedModule.add(eqModule)
                    }
                }
            }
        }
        return equippedModule
    }

    fun getWeaponEntity(weaponId: String): WeaponEntity{
        dataSource.connection.use { conn ->
            conn.prepareStatement(
                """
                    SELECT 
                    weapon_id, image_Url, weapon_Name, weapon_PerkAbilityDescription, weapon_PerkAbilityImageUrl, 
                    weapon_PerkAbilityName, weapon_RoundsType, weapon_Tier, weapon_Type 
                    FROM WeaponEntity 
                    WHERE weapon_id = ?
                """.trimIndent()
            ).use { stmt ->
                stmt.setString(1, weaponId)
                stmt.executeQuery().use { rs ->
                    if(rs.next()){
                        return WeaponEntity (
                            weaponId = rs.getString("weapon_id"),
                            imageUrl = rs.getString("image_Url"),
                            weaponName = rs.getString("weapon_Name"),
                            weaponPerkAbilityDescription = rs.getString("weapon_PerkAbilityDescription") ?: "",
                            weaponPerkAbilityImageUrl = rs.getString("weapon_PerkAbilityImageUrl") ?: "",
                            weaponPerkAbilityName = rs.getString("weapon_PerkAbilityName") ?: "",
                            weaponRoundsType = rs.getString("weapon_RoundsType"),
                            weaponTier = rs.getString("weapon_Tier"),
                            weaponType = rs.getString("weapon_Type")
                        )
                    }
                }
            }
        }

        return WeaponEntity()
    }

    fun getEquippedReactor(
        reactorId: String,
        level: Int,
        enchantLevel: Int
    ): EquippedReactor{
        var equippedReactor = EquippedReactor()
        val fixedWhere =
            if (enchantLevel == 0) "" else "and EE.enchant_level = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement("""
                SELECT
                    R.reactor_id,
                    '1' as slotId,
                    R.reactor_name,
                    R.reactor_tier,
                    SP.level,
                    EE.enchant_level,
                    EE.stat_type,
                    EE.value,
                    SP.skill_atk_power,
                    SP.sub_skill_atk_power,
                    R.optimized_condition_type,
                    R.image_url
                FROM ReactorEntity R 
                INNER JOIN ReactorSkillPowerEntity SP on R.reactor_id = SP.reactor_id 
                INNER JOIN ReactorEnchantEffectEntity EE on R.reactor_id = EE.reactor_id and SP.level = EE.level 
                WHERE R.reactor_id = ? and SP.level = ? $fixedWhere
            """.trimIndent()
            ).use { stmt ->
                stmt.setString(1, reactorId)
                stmt.setInt(2, level)
                if(enchantLevel > 0) stmt.setInt(3, enchantLevel)
                stmt.executeQuery().use { rs ->
                    if(rs.next()){
                         equippedReactor = EquippedReactor(
                            reactorId = rs.getString(1),
                            reactorSlotId = rs.getString(2),
                            reactorName = rs.getString(3),
                            reactorTier = rs.getString(4),
                            level = rs.getInt(5),
                            reactorEnchantLevel = rs.getInt(6),
                            statTypeByLevelAndEnchantLevel = rs.getString(7),
                            statValueByLevelAndEnchantLevel = rs.getDouble(8),
                            skillAtkPower = rs.getString(9),
                            subSkillAtkPower = rs.getString(10),
                            optimizedConditionType = rs.getString(11) ?: "",
                            imageUrl = rs.getString(12)
                        )
                    }
                }
            }

            conn.prepareStatement("""
                SELECT
                    coefficient_stat_id,
                    coefficient_stat_value
                FROM ReactorSkillPowerCoefficientEntity
                WHERE reactor_id = ? and level = ?
            """.trimIndent()
            ).use {stmt ->
                stmt.setString(1, reactorId)
                stmt.setInt(2, level)
                stmt.executeQuery().use { rs ->
                    while(rs.next()){
                        equippedReactor.coefficientList.add(
                            ReactorCoefficient(
                                coefficientStatId = rs.getString(1),
                                coefficientStatValue = rs.getDouble(2)
                            )
                        )
                    }
                }
            }
        }

        return equippedReactor
    }

    fun getEquippedExternal(externals: List<ExternalComponent>): List<EquippedExternal> {
        val equipExternals: MutableList<EquippedExternal> = mutableListOf()
        val whereCondition = externals.joinToString(","){
            "('${it.externalComponentId}', ${it.externalComponentLevel})"
        }

        dataSource.connection.use { conn ->
            conn.autoCommit = false
            
            //장착 외장부품 추가
            conn.prepareStatement("""
                SELECT
                    ex.external_component_id,
                    ex.external_component_name,
                    ex.image_url,
                    ex.external_component_equipment_type,
                    ex.external_component_tier,

                    stat.stat_name,
                    exBase.stat_value

                FROM ExternalCompEntity ex
                INNER JOIN ExternalCompBaseStatEntity exBase ON ex.external_component_id = exBase.external_component_id
                INNER JOIN StatEntity stat ON exBase.stat_id = stat.stat_id
                WHERE (ex.external_component_id, exBase.level) IN ($whereCondition)
            """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while(rs.next()){
                        val ex = EquippedExternal (
                            externalComponentId = rs.getString(1),
                            externalComponentName = rs.getString(2),
                            imageUrl = rs.getString(3),
                            externalComponentEquipmentType = rs.getString(4),
                            externalComponentTier = rs.getString(5),
                            statName = rs.getString(6),
                            statValue = rs.getDouble(7)
                        )
                        equipExternals.add(ex)
                    }
                }
            } // 장착 외장부품 추가
            
            // 활성화된 세트 및 세트 설명
            val whereCondition2 = externals.joinToString(",") {
                "'${it.externalComponentId}'"
            }
            conn.prepareStatement("""
                SELECT
                    (count(*)/2) as enabledCount,
                    A.set_option,
                    A.set_count,
                    A.set_option_effect,
                    B.set_count,
                    B.set_option_effect
                FROM ExternalCompSetOptionEntity A
                INNER JOIN ExternalCompSetOptionEntity B
                    ON A.external_component_id = B.external_component_id AND B.set_count = 4
                WHERE A.external_component_id
                          IN ($whereCondition2)
                GROUP BY A.set_option
            """.trimIndent()
            ).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while(rs.next()){
                        val enabledSet = EnabledSet(
                            enabledCount = rs.getInt(1),
                            setOption = rs.getString(2),
                            setOne = rs.getInt(3),
                            setOneDesc = rs.getString(4),
                            setTwo = rs.getInt(5),
                            setTwoDesc = rs.getString(6)
                        )
                        //첫번째값에 설명추가
                        equipExternals[0].setOptions.add(enabledSet)
                    }
                }
            }
        }
        
        return equipExternals
    }
}
package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepository

//분리전 rawString DTO
data class RawModuleStat(
    val moduleId: String,
    val level: Int,
    val value: String,
)

//분리된 DTO
data class ModuleStatCalc(
    val moduleId: String,
    val level: Int,
    val type: String,
    val value: String
)

object ExtractStatHelper {
    private val localRepository = LocalRepository(DatabaseFactory.dataSource)

    //총기 치명타 확률
    val criHitRateList: MutableList<Int> = mutableListOf()
    //총기 치명타 배율
    val criHitDmgList: MutableList<Int> = mutableListOf()
    //스킬 치명타 확률
    val skillHitRateList: MutableList<Int> = mutableListOf()
    //스킬 치명타 배율
    val skillHitDmgList: MutableList<Int> = mutableListOf()
    //스킬 자원 소모량
    val skillCostList: MutableList<Int> = mutableListOf()
    //최대 체력
    val maxHpList: MutableList<Int> = mutableListOf()

    init {
        setupList()
    }

    fun initModuleStatCalc() {
        val time = System.currentTimeMillis()
//        criHitRateList.forEach { setupModuleStatCalc(it.toString()) }
//        criHitDmgList.forEach { setupModuleStatCalc(it.toString()) }
//        skillHitRateList.forEach { setupModuleStatCalc(it.toString()) }
//        skillHitDmgList.forEach { setupModuleStatCalc(it.toString()) }
//        skillCostList.forEach { setupModuleStatCalc(it.toString()) }
        maxHpList.forEach { setupModuleStatCalc(it.toString()) }

        val gap = System.currentTimeMillis() - time

        com.example.logger.info("Time: $gap")
    }

    private fun setupModuleStatCalc(moduleId: String){
        val list = localRepository.getRawModuleStat(moduleId)
        val trans = mutableListOf<ModuleStatCalc>()

        list.map {raw ->
            val extract = extractValue(raw.moduleId.toInt(), raw.value)

            extract?.forEach {
                val module = ModuleStatCalc(
                    moduleId = raw.moduleId,
                    level = raw.level,
                    type = it.key,
                    value = it.value.toString()
                )
                trans.add(module)
            }
        }

        localRepository.insertModuleStatCalc(trans)
    }

    private fun extractValue(moduleId: Int, raw: String): Map<String, Double?>?{
        val map = mutableMapOf<String, Double?>()
        if(criHitRateList.contains(moduleId)) {
            val regex = """총기 치명타 확률\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)

            map["crihitrate"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        if(criHitDmgList.contains(moduleId)) {
            val regex = """총기 치명타 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)
            map["crihitdmg"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        if(skillHitRateList.contains(moduleId)) {
            val regex = """스킬 치명타 확률\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)
            map["skillhitrate"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        if(skillHitDmgList.contains(moduleId)) {
            val regex = """스킬 치명타 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)
            map["skillhitdmg"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        if(skillCostList.contains(moduleId)) {
            val regex = """스킬 자원 소모량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)
            map["skillcost"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        if(maxHpList.contains(moduleId)) {
            val regex = """최대 체력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            val matchResult = regex.find(raw)
            map["maxhp"] = matchResult?.groupValues?.get(1)?.toDouble()
        }

        return map
    }

    /**
     * 관련 있는 module_id 목록 셋업
     * */
    private fun setupList(){
        criHitRateList.addAll(listOf(
            252012024, 252012053, 252012056, 252012057, 252012067, 252012072, 252012078, 252012082,
            252021007, 252022053, 252022062, 252022067, 252022072, 252022082, 252031007, 252032056,
            252032058, 252032062, 252032082, 252042019, 252042024, 252042029, 252042040, 252042041,
            252042042, 252042062, 252042075, 252011007, 252012045, 252012050, 252012058, 252012062,
            252022024, 252022045, 252022050, 252022056, 252022057, 252022058, 252022078, 252032024,
            252032045, 252032050, 252032053, 252032057, 252032067, 252032072, 252032078, 252041010,
            252042014, 252042034, 252042039, 252042047, 252042055, 252042071
        ))

        criHitDmgList.addAll(listOf(
            252012047, 252012053, 252012061, 252012066, 252012071, 252012077, 252022018, 252022047,
            252022051, 252022053, 252022055, 252022061, 252022077, 252022081, 252032054, 252032058,
            252032071, 252032077, 252032081, 252042016, 252042026, 252042030, 252042035, 252042037,
            252042041, 252042074, 252011015, 252012018, 252012051, 252012054, 252012055, 252012058,
            252012081, 252021015, 252022054, 252022058, 252022066, 252022071, 252031015, 252032018,
            252032047, 252032051, 252032053, 252032055, 252032061, 252032066, 252041017, 252042021,
            252042034, 252042036, 252042046, 252042054, 252042061, 252042070
        ))

        skillHitRateList.addAll(listOf(
            251002094, 251001028, 251002095
        ))

        skillHitDmgList.addAll(listOf(
            251002094, 251001029, 251002095
        ))

        skillCostList.addAll(listOf(
            251002018, 251002033, 251002101, 251002093, 251001003
        ))

        maxHpList.addAll(listOf(
            251001009, 251002020, 251002021, 251002022, 251002029, 251002080, 251002100, 251002008,
            251002010, 251002011, 251002012, 251002023, 251002024
        ))
    }
}
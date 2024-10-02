package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepository
import com.example.logger
import kotlinx.coroutines.*

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
    //방어력
    private lateinit var defList: MutableList<Int>
    //최대 실드
    private lateinit var maxShieldList: MutableList<Int>
    
    //받는 정신력 회복량
    private lateinit var mpRecoveryList: MutableList<Int>
    //받는 체력 회복량
    private lateinit var hpRecoveryList: MutableList<Int>
    //주는 체력 회복량
    private lateinit var hpHealList: MutableList<Int>
    //받는 피해량 계수
    private lateinit var incomingDmgModList: MutableList<Int>
    //주는 피해량 계수
    private lateinit var outgoingDmgModList: MutableList<Int>

    //재장전 시간 계수(Reload Time Modifier)
    private lateinit var reloadModList: MutableList<Int>
    
    //약점 배율 (Weak Point Damage)
    private lateinit var weakDmgList: MutableList<Int>
    //정확도 (Accuracy)
    private lateinit var accuracyList: MutableList<Int>
    //총기 공격력
    private lateinit var firearmAtkList: MutableList<Int>
    //장탄량 (Rounds Per Magazine)
    private lateinit var rpMagazineList: MutableList<Int>
    //반동 (Recoil)
    private lateinit var recoilList: MutableList<Int>
    //발사 속도(Fire Rate)
    private lateinit var fireRateList: MutableList<Int>
    //일반탄 최대량 (Max General Rounds)
    private lateinit var maxGeneralList: MutableList<Int>

    //충격탄 최대량 (Max Impact Rounds)
    private lateinit var maxImpactList: MutableList<Int>
    //특수탄 최대량 (Max Special Rounds)
    private lateinit var maxSpecialList: MutableList<Int>
    //고위력탄 최대량 (Max High-Power Rounds)
    private lateinit var maxHPowerList: MutableList<Int>

    //이동속도(Movement Speed)
    private lateinit var movementSpeedList: MutableList<Int>
    //조준중 이동속도(Movement Speed While Aiming)
    private lateinit var movementSpeedWhileAimingList: MutableList<Int>
    //조준시 약점 배율(Weak Point DMG when Aiming)
    private lateinit var weakPointDmgAiming: MutableList<Int>
    //무기 교체 속도(Weapon Change Speed)
    private lateinit var weaponChangeList: MutableList<Int>

    //총기 치명타 확률
    private val criHitRateList: MutableList<Int> = mutableListOf()
    //총기 치명타 배율
    private val criHitDmgList: MutableList<Int> = mutableListOf()

    //스킬 치명타 확률
    private val skillHitRateList: MutableList<Int> = mutableListOf()
    //스킬 위력
    private val skillDmgList: MutableList<Int> = mutableListOf()
    //스킬 위력 계수
    private val skillDmgModList: MutableList<Int> = mutableListOf()

    //스킬 치명타 배율
    private val skillHitDmgList: MutableList<Int> = mutableListOf()
    //스킬 자원 소모량
    private val skillCostList: MutableList<Int> = mutableListOf()
    //스킬 재사용 대기시간
    private val skillCdList: MutableList<Int> = mutableListOf()
    //스킬 효과 범위
    private val skillEffectRangeList: MutableList<Int> = mutableListOf()
    //스킬 지속시간
    private val skillDurationList: MutableList<Int> = mutableListOf()

    /** 속성 스킬 위력 */
    private lateinit var nonAttrSkillPowerList: MutableList<Int> // 무속성
    private lateinit var chillSkillPowerList: MutableList<Int> // 냉기
    private lateinit var fireSkillPowerList: MutableList<Int> // 화염
    private lateinit var toxicSkillPowerList: MutableList<Int>  // 독
    private lateinit var elecSkillPowerList: MutableList<Int> // 전기
    /** 속성 스킬 위력 */

    /** 스킬 종류 */
    private lateinit var techSkillPowerList: MutableList<Int> // 공학
    private lateinit var singularSkillPowerList: MutableList<Int> // 특이
    private lateinit var fusionSkillPowerList: MutableList<Int> // 융합
    private lateinit var dimenSkillPowerList: MutableList<Int> // 차원
    /** 스킬 종류 */

    //최대 체력
    private val maxHpList: MutableList<Int> = mutableListOf()
    //최대 정신력
    private val maxMpList: MutableList<Int> = mutableListOf()

    init {
        setupList()
    }

    fun initModuleStatCalc() {
        /**계승자 관련 리스트*/
        val descendantList = listOf(
            maxHpList, maxMpList, defList,
            maxShieldList, mpRecoveryList, hpRecoveryList,
            hpHealList, incomingDmgModList, outgoingDmgModList,
            movementSpeedList, movementSpeedWhileAimingList
        )

        /**스킬 관련 리스트*/
        val skillList = listOf(
            skillHitRateList, skillHitDmgList, skillCostList,
            skillCdList, skillDmgList, skillDmgModList,
            skillEffectRangeList, skillDurationList,
            
            //스킬 속성
            chillSkillPowerList, toxicSkillPowerList, fireSkillPowerList,
            elecSkillPowerList, nonAttrSkillPowerList,

            // 스킬 종류
            techSkillPowerList, singularSkillPowerList, fusionSkillPowerList,
            dimenSkillPowerList,
        )
        
        /**무기 관련 리스트*/
        val weaponList = listOf(
            criHitRateList, criHitDmgList, firearmAtkList, rpMagazineList, recoilList,
            fireRateList, weakDmgList, accuracyList, reloadModList, maxGeneralList,
            maxImpactList, maxSpecialList, maxHPowerList, weakPointDmgAiming,
            weaponChangeList
        )

        startRoutine(descendantList, skillList, weaponList)
    }

    private fun startRoutine( vararg lists: List<MutableList<Int>> ) {
        CoroutineScope(Dispatchers.IO).launch {
            logger.info("### Raw ModuleStat -> Calculated ModuleStat 작업 시작 [HEAVY] ###")
            val time = System.currentTimeMillis()
             lists.forEachIndexed { idx, varargs ->
                 varargs.forEach { list ->
                     list.forEach { setupModuleStatCalc(it.toString()) }
                 }
                 logger.info(" ### 스탯분리 varargs INDEX $idx DONE ")
            }
            val gap = System.currentTimeMillis() - time
            logger.info("### Raw ModuleStat -> Calculated ModuleStat 소요 시간: $gap ms ###")
        }
    }
    private fun setupModuleStatCalc(moduleId: String){
        val list = localRepository.getRawModuleStat(moduleId)
        val trans = mutableListOf<ModuleStatCalc>()

        list.map {raw ->
            val extract = extractValue(raw.moduleId.toInt(), raw.value)

            extract.forEach {
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

    private fun extractPattern(raw: String, regex: Regex): Double? {
        val matchResult = regex.find(raw)
        return matchResult?.groupValues?.get(1)?.toDouble()
    }

    private fun extractValue(moduleId: Int, raw: String): Map<String, Double?>{
        val map = mutableMapOf<String, Double?>()
        if(criHitRateList.contains(moduleId)) {
            val regex = """총기 치명타 확률\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["crihitrate"] = extractPattern(raw, regex)
        }

        if(criHitDmgList.contains(moduleId)) {
            val regex = """총기 치명타 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["crihitdmg"] = extractPattern(raw, regex)
        }

        if(skillHitRateList.contains(moduleId)) {
            val regex = """스킬 치명타 확률\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skillhitrate"] = extractPattern(raw, regex)
        }

        if(skillHitDmgList.contains(moduleId)) {
            val regex = """스킬 치명타 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skillhitdmg"] = extractPattern(raw, regex)
        }

        if(skillCostList.contains(moduleId)) {
            val regex = """스킬 자원 소모량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skillcost"] = extractPattern(raw, regex)
        }

        if(maxHpList.contains(moduleId)) {
            val regex = """최대 체력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxhp"] = extractPattern(raw, regex)
        }

        if(maxMpList.contains(moduleId)) {
            val regex = """최대 정신력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxmp"] = extractPattern(raw, regex)
        }

        if(skillDmgList.contains(moduleId)) {
            val regex = """스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skilldmg"] = extractPattern(raw, regex)
        }
        if(skillDmgModList.contains(moduleId)) {
            val regex = """스킬 위력 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skilldmgmod"] = extractPattern(raw, regex)
        }

        if(skillCdList.contains(moduleId)) {
            val regex = """스킬 재사용 대기시간\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skillcd"] = extractPattern(raw, regex)
        }

        if(skillEffectRangeList.contains(moduleId)) {
            val regex = """스킬 효과 범위\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skilleffectrange"] = extractPattern(raw, regex)
        }

        if(skillDurationList.contains(moduleId)) {
            val regex = """스킬 지속시간\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["skillduration"] = extractPattern(raw, regex)
        }
        
        /**스킬 속성*/
        if(fireSkillPowerList.contains(moduleId)) {
            val regex = """화염 속성 스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["fireskillpower"] = extractPattern(raw, regex)
        }
        if(toxicSkillPowerList.contains(moduleId)) {
            val regex = """독 속성 스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["toxicskillpower"] = extractPattern(raw, regex)
        }
        if(nonAttrSkillPowerList.contains(moduleId)) {
            val regex = """무 속성 스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["nonattrskillpower"] = extractPattern(raw, regex)
        }
        if(chillSkillPowerList.contains(moduleId)) {
            val regex = """냉기 속성 스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["chillskillpower"] = extractPattern(raw, regex)
        }
        if(elecSkillPowerList.contains(moduleId)) {
            val regex = """전기 속성 스킬 위력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["elecskillpower"] = extractPattern(raw, regex)
        }
        /**스킬 속성*/


        /**스킬 종류*/
        if(techSkillPowerList.contains(moduleId)) {
            val regex = """공학 스킬 위력 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["techskillpowermod"] = extractPattern(raw, regex)
        }
        if(singularSkillPowerList.contains(moduleId)) {
            val regex = """특이 스킬 위력 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["singularskillpowermod"] = extractPattern(raw, regex)
        }
        if(fusionSkillPowerList.contains(moduleId)) {
            val regex = """융합 스킬 위력 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["fusionskillpowermod"] = extractPattern(raw, regex)
        }
        if(dimenSkillPowerList.contains(moduleId)) {
            val regex = """차원 스킬 위력 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["dimenskillpowermod"] = extractPattern(raw, regex)
        }
        /**스킬 종류*/

        if(defList.contains(moduleId)) {
            val regex = """방어력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["def"] = extractPattern(raw, regex)
        }

        if(maxShieldList.contains(moduleId)) {
            val regex = """최대 실드\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxshield"] = extractPattern(raw, regex)
        }

        if(firearmAtkList.contains(moduleId)) {
            val regex = """총기 공격력\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["firearmatk"] = extractPattern(raw, regex)
        }

        if(mpRecoveryList.contains(moduleId)) {
            val regex = """받는 정신력 회복량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["mprecovery"] = extractPattern(raw, regex)
        }

        if(hpRecoveryList.contains(moduleId)) {
            val regex = """받는 체력 회복량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["hprecovery"] = extractPattern(raw, regex)
        }

        if(hpHealList.contains(moduleId)) {
            val regex = """주는 체력 회복량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["hpheal"] = extractPattern(raw, regex)
        }

        if(incomingDmgModList.contains(moduleId)) {
            val regex = """받는 피해량 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["incomingdmgmod"] = extractPattern(raw, regex)
        }

        if(outgoingDmgModList.contains(moduleId)) {
            val regex = """주는 피해량 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["outgoingdmgmod"] = extractPattern(raw, regex)
        }

        if(recoilList.contains(moduleId)) {
            val regex = """반동\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["recoil"] = extractPattern(raw, regex)
        }

        if(rpMagazineList.contains(moduleId)) {
            val regex = """장탄량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["rpmagazine"] = extractPattern(raw, regex)
        }

        if(fireRateList.contains(moduleId)) {
            val regex = """발사 속도\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["firerate"] = extractPattern(raw, regex)
        }

        if(weakDmgList.contains(moduleId)) {
            val regex = """약점 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["weakpointdmg"] = extractPattern(raw, regex)
        }

        if(accuracyList.contains(moduleId)) {
            val regex = """정확도\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["accuracy"] = extractPattern(raw, regex)
        }
        
        if(reloadModList.contains(moduleId)){
            val regex = """재장전 시간 계수\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["reloadmod"] = extractPattern(raw, regex)
        }


        if(maxGeneralList.contains(moduleId)){
            val regex = """일반탄 최대량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxgeneral"] = extractPattern(raw, regex)
        }
        if(maxImpactList.contains(moduleId)){
            val regex = """충격탄 최대량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maximpact"] = extractPattern(raw, regex)
        }
        if(maxHPowerList.contains(moduleId)){
            val regex = """고위력탄 최대량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxhpower"] = extractPattern(raw, regex)
        }
        if(maxSpecialList.contains(moduleId)){
            val regex = """특수탄 최대량\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["maxspecial"] = extractPattern(raw, regex)
        }


        if(movementSpeedList.contains(moduleId)){
            val regex = """이동 속도\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["movementspeed"] = extractPattern(raw, regex)
        }
        if(movementSpeedWhileAimingList.contains(moduleId)){
            val regex = """조준 중 이동 속도\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["movementspeedwhileaiming"] = extractPattern(raw, regex)
        }
        if(weakPointDmgAiming.contains(moduleId)){
            val regex = """조준 시 약점 배율\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["weakpointaiming"] = extractPattern(raw, regex)
        }
        if(weaponChangeList.contains(moduleId)){
            val regex = """무기 교체 속도\s*([+-]?\d+(\.\d+)?)%""".toRegex()
            map["weaponchangespeed"] = extractPattern(raw, regex)
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
        firearmAtkList = mutableListOf(
            251002041, 252012004, 252012005, 252012024, 252012047, 252012052, 252012056, 252012059,
            252012069, 252012079, 252022004, 252022005, 252022046, 252022047, 252022059, 252022069,
            252022075, 252022079, 252031001, 252032004, 252032052, 252032054, 252032056, 252032064,
            252032069, 252041001, 252042016, 252042017, 252042018, 252042019, 252042020, 252042022,
            252042035, 252042042, 252042044, 252042059, 252042068, 252042072, 252011001, 252012045,
            252012046, 252012054, 252012064, 252012075, 252021001, 252022024, 252022045, 252022052,
            252022054, 252022056, 252022064, 252032005, 252032024, 252032045, 252032046, 252032047,
            252032059, 252032075, 252032079, 252041002, 252042014, 252042015, 252042021, 252042023,
            252042031, 252042039, 252042052
        )

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

        maxMpList.addAll(listOf(
            251002016, 251002018, 251002020, 251002097, 251001033, 251002012, 251002015, 251002017,
            251002030, 251002091
        ))

        maxShieldList = mutableListOf(
            251002014, 251002101, 251002104, 251001010, 251002008,
            251002010, 251002015
        )

        skillDmgList.addAll(listOf(
            251002057, 251002058, 251002065, 251001035, 251002037, 251002059, 251002060, 251002061,
            251002062, 251002063, 251002064, 251002096, 251002098
        ))

        skillDmgModList.addAll(listOf(
            251001004, 251002016, 251002033, 251002034, 251002035, 251002036, 251002041, 251002099
        ))

        skillCdList.addAll(listOf(
            251001002, 251002036, 251002067, 251002069, 251002071, 251002072, 251002073, 251002080,
            251002017, 251002037, 251002066, 251002068, 251002070, 251002074
        ))

        skillEffectRangeList.addAll(listOf(
            251001015, 251002035, 251002104, 251002093
        ))

        skillDurationList.addAll(listOf(
            251002034, 251002092, 251002100, 251001016, 251002091
        ))

        /**속성 스킬 위력*/
        fireSkillPowerList = mutableListOf( 251001020, 251002061, 251002070, 251002112 )
        chillSkillPowerList = mutableListOf( 251002071, 251002113, 251001021, 251002062 )
        nonAttrSkillPowerList = mutableListOf( 251002065, 251002116, 251001019, 251002074 )
        toxicSkillPowerList = mutableListOf( 251002073, 251002114, 251001023, 251002064 )
        elecSkillPowerList = mutableListOf ( 251001022, 251002072, 251002115, 251002063 )
        /**속성 스킬 위력*/

        /**스킬 종류*/
        techSkillPowerList = mutableListOf ( 251001027, 251002069, 251002060 )
        singularSkillPowerList = mutableListOf ( 251002058, 251002067, 251001025 )
        fusionSkillPowerList = mutableListOf ( 251002057, 251001024, 251002066 )
        dimenSkillPowerList = mutableListOf ( 251001026, 251002059, 251002068 )
        /**스킬 종류*/

        defList = mutableListOf(
            251002014, 251002097, 251003010, 251001011, 251002011, 251002042,
            251002098, 251002099
        )

        mpRecoveryList = mutableListOf( 251001031, 251002102 )
        hpRecoveryList = mutableListOf( 251002103 )
        hpHealList = mutableListOf( 251002092, 251002105, 251001032, 251002096 )
        incomingDmgModList = mutableListOf ( 251002029, 251002043, 251002102, 251002103, 251002105, 251002030 )
        outgoingDmgModList = mutableListOf ( 251002043, 251002044, 251002042 )

        recoilList = mutableListOf(
            252012005, 252012069, 252012070, 252012071, 252012072, 252021003,
            252022005, 252022069, 252022070, 252022072, 252031003, 252032069,
            252032070, 252032071, 252042018, 252042028, 252042059, 252042062,
            252011003, 252022071, 252032005, 252032072, 252041005, 252042023,
            252042060, 252042061
        )

        rpMagazineList = mutableListOf(
            252012059, 252012061, 252021014, 252022059, 252022061, 252022062,
            252032060, 252032062, 252042044, 252042045, 252011014, 252012029,
            252012060, 252012062, 252022029, 252022060, 252031014, 252032029,
            252032059, 252032061, 252041016, 252042046, 252042047, 252042048
        )

        fireRateList = mutableListOf(
            252012004, 252012066, 252012067, 252012083, 252012085, 252012086,
            252013019, 252013021, 252021002, 252022004, 252022065, 252022067,
            252022083, 252022085, 252023031, 252031002, 252032004, 252032064,
            252032065, 252032086, 252033021, 252042017, 252042022, 252042083,
            252042085, 252042086, 252011002, 252012064, 252012065, 252012084,
            252022064, 252022066, 252022084, 252022086, 252023029, 252032066,
            252032067, 252032083, 252032084, 252032085, 252033039, 252041004,
            252042027, 252042052, 252042053, 252042054, 252042055, 252042084
        )

        weakDmgList = mutableListOf(
            252012035, 252012052, 252012057, 252012070, 252022035, 252022046,
            252022051, 252022055, 252022065, 252022070, 252032052, 252032060,
            252032065, 252032070, 252032080, 252041019, 252042020, 252042029,
            252042030, 252042040, 252042045, 252042073, 252011016, 252012029,
            252012046, 252012050, 252012051, 252012055, 252012060, 252012065,
            252012076, 252012080, 252021017, 252022029, 252022050, 252022052,
            252022057, 252022060, 252022076, 252022080, 252031017, 252032029,
            252032035, 252032046, 252032050, 252032051, 252032055, 252032057,
            252032076, 252042015, 252042025, 252042031, 252042032, 252042036,
            252042048, 252042053, 252042060, 252042069
        )

        accuracyList = mutableListOf(
            252011008, 252012035, 252012079, 252012082, 252013018, 252022035,
            252022079, 252022081, 252022082, 252032080, 252032081, 252032082,
            252033038, 252041011, 252042072, 252042073, 252042074, 252042075,
            252043048, 252012080, 252012081, 252021008, 252022080, 252023028,
            252031008, 252032035, 252032079, 252042032
        )

        reloadModList = mutableListOf(
            252012022, 252012077, 252012078, 252021013, 252022018, 252022019,
            252022022, 252022075, 252022077, 252032019, 252032021, 252032077,
            252042037, 252042068, 252042080, 252042081, 252042082, 252011013,
            252012018, 252012019, 252012020, 252012021, 252012075, 252012076,
            252022020, 252022021, 252022076, 252022078, 252031012, 252032018,
            252032020, 252032022, 252032075, 252032076, 252032078, 252041014,
            252042069, 252042070, 252042071, 252042079
        )

        maxGeneralList = mutableListOf( 252012031, 252011010 )
        maxImpactList = mutableListOf( 252031010, 252032031 )
        maxSpecialList = mutableListOf( 252021010, 252022031 )
        maxHPowerList = mutableListOf( 252041009, 252042013 )
        weaponChangeList = mutableListOf( 252011011, 252021011, 252013009, 252031011, 252033009, 252041013 )
        movementSpeedList = mutableListOf( 252012031, 252022031, 252032031, 252042013 )
        movementSpeedWhileAimingList = mutableListOf( 252012011, 252021016, 252031016, 252033013, 252041018, 252043014 )
        weakPointDmgAiming = mutableListOf( 252033013, 252043014 )

    }
}
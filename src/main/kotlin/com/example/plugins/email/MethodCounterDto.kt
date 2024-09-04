package com.example.plugins.email

import kotlin.reflect.KMutableProperty0

object MethodCounterDto{
    var basicInfo: Int = 0
    var eqModule: Int = 0
    var weaponEntity: Int = 0
    var eqReactor: Int = 0
    var eqExternal: Int = 0
    var denied: Int = 0
}

enum class EndpointEnum(val value: String, val field: KMutableProperty0<Int>) {
    BASIC_INFO("basic_info", MethodCounterDto::basicInfo),
    EQ_MODULE("equipped_module", MethodCounterDto::eqModule),
    WEAPON_ENTITY("weapon_entity", MethodCounterDto::weaponEntity),
    EQ_REACTOR("equipped_reactor", MethodCounterDto::eqReactor),
    EQ_EXTERNAL("equipped_external", MethodCounterDto::eqExternal),
    DENIED("denied", MethodCounterDto::denied)
}
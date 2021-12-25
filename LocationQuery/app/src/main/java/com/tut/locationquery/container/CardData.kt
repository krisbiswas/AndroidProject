package com.tut.locationquery.container

import android.location.Address

data class CardData(var pinData: Address?=null, var title: String, var address: String, var distance: String)

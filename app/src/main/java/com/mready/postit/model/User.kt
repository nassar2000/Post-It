package com.mready.postit.model

class User (val id:Int ?= null,
            val username:String ?= null,
            val displayName:String ?= null,
            val cratedBy: String? = null,
            val token:String ?= null)
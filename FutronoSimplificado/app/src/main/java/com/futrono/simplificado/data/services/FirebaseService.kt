package com.futrono.simplificado.data.services

import com.futrono.simplificado.data.models.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun signInUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            
            val userDoc = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()
            
            val firebaseUser = if (userDoc.exists()) {
                userDoc.toObject<FirebaseUser>() ?: FirebaseUser(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    isEmailVerified = user.isEmailVerified,
                    roles = listOf("cliente")
                )
            } else {
                val newUser = FirebaseUser(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    isEmailVerified = user.isEmailVerified,
                    roles = listOf("cliente")
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
                newUser
            }
            
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUserWithRoles(): Result<FirebaseUser?> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val userDoc = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()
                
                val firebaseUser = if (userDoc.exists()) {
                    userDoc.toObject<FirebaseUser>() ?: FirebaseUser(
                        id = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: "",
                        phoneNumber = user.phoneNumber ?: "",
                        isEmailVerified = user.isEmailVerified,
                        roles = listOf("cliente")
                    )
                } else {
                    FirebaseUser(
                        id = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: "",
                        phoneNumber = user.phoneNumber ?: "",
                        isEmailVerified = user.isEmailVerified,
                        roles = listOf("cliente")
                    )
                }
                Result.success(firebaseUser)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


package com.maximus.moodtrack.data.repository

import android.content.SharedPreferences
import com.maximus.moodtrack.data.model.User
import com.maximus.moodtrack.util.FireStoreCollection
import com.maximus.moodtrack.util.SharedPrefConstants
import com.maximus.moodtrack.util.UiState
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appPreferences: SharedPreferences,
    val gson: Gson
) : AuthRepository {

    override fun registerUser(
        email: String,
        password: String,
        user: User, result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.id = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when (state) {
                            is UiState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null) {
                                        result.invoke(UiState.Failure("Успешная регистрация, но сессия не сохранена"))
                                    } else {
                                        result.invoke(
                                            UiState.Success("Успешная регистрация!")
                                        )
                                    }
                                }
                            }

                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }
                        }
                    }
                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Ошибка аутентификации")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Не удалось выполнить аутентификацию, пароль должен содержать не менее 6 символов"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Ошибка аутентификации, введен неверный адрес электронной почты"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Ошибка аутентификации, адрес электронной почты уже зарегистрирован."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Данные пользователя успешно изменены")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }


    }

    override fun udpateUserData(
        user: User, result: (UiState<String>) -> Unit
    ) {
        updateUserInfo(user) { state ->
            when (state) {
                is UiState.Success -> {
                    storeSession(id = user.id) {
                        if (it == null) {
                            result.invoke(UiState.Failure("Данные сохранен, но сессия не сохранена"))
                        } else {
                            result.invoke(
                                UiState.Success("Данные сохранены!")
                            )
                        }
                    }
                }

                is UiState.Failure -> {
                    result.invoke(UiState.Failure(state.error))
                }
            }
        }
    }


    override fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: "") {
                        if (it == null) {
                            result.invoke(UiState.Failure("Ошибка сохранения сессии"))
                        } else {
                            result.invoke(UiState.Success("Успешный вход!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Ошибка аутентификации, проверьте адрес электронной почты и пароль"))
            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("Письмо было отправленно"))

                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Ошибка аутентификации, проверьте электронную почту"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(user)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (user_str == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
        }
    }

}
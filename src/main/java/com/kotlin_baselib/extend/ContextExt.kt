package com.kotlin_baselib.extend

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager


fun Context.getSharedPreference(): SharedPreferences {
    return this.getSharedPreferences("default", Activity.MODE_PRIVATE)
}

fun SharedPreferences.edit(action: (SharedPreferences.Editor) -> Unit) {
    edit().also(action).apply()
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    try {
        beginTransaction().apply {
            action()
        }.commitNowAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

inline fun FragmentManager.transactNotNow(action: FragmentTransaction.() -> Unit) {
    try {
        beginTransaction().apply {
            action()
        }.commitAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentManager.show(fragment: Fragment) {
    beginTransaction().show(fragment).commitAllowingStateLoss()
}

fun FragmentManager.hide(fragment: Fragment) {
    try {
        beginTransaction().hide(fragment).commitAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentManager.hideAndShow(action: FragmentTransaction.() -> Unit) {
    try {
        beginTransaction().apply {
            action()
        }.commitAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun FragmentManager.showAndHideFragments(
    showFragment: Fragment?,
    hideFragments: List<Fragment?>?,
    action: () -> Unit
) {
    try {
        val b = beginTransaction()
        hideFragments?.forEach {
            it?.let {
                b.hide(it)
            }
        }
        showFragment?.let {
            b.show(it)
        }
        b.apply {
            action()
        }.commitAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    try {
        supportFragmentManager.transact {
            replace(frameId, fragment)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(@IdRes id: Int, fragment: Fragment, tag: String) {
    try {
        if (!fragment.isAdded) {
            supportFragmentManager.transact {
                add(id, fragment, tag)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    try {
        if (!fragment.isAdded) {
            supportFragmentManager.transact {
                add(fragment, tag)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun Fragment.addFragmentToFragment(fragment: Fragment, tag: String) {
    try {
        if (!fragment.isAdded) {
            childFragmentManager.transact {
                add(fragment, tag)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun FragmentTransaction.hideFragments(vararg fragment: Fragment, action: () -> Unit) {
    try {
        fragment.forEach {
            hide(it)
        }
        apply {
            action()
        }.commitAllowingStateLoss()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> AppCompatActivity.startActivity(clazz: Class<T>) {
    startActivity(Intent(this, clazz))
}

/**
 * quick open new Activity with Anim
 */
fun <T : AppCompatActivity> AppCompatActivity.startActivitysAnim(
    clazz: Class<T>,
    bundle: Bundle?,
    requestCode: Int? = null,
    enterAnim: Int? = null,
    exitAnim: Int? = null
) {
    val intent = Intent(this, clazz)
    bundle?.let {
        intent.putExtras(bundle)
    }
    if (requestCode != null) {
        startActivityForResult(intent, requestCode)
    } else {
        startActivity(intent)

    }

    if (enterAnim != null && exitAnim != null) {
        overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> AppCompatActivity.startActivityForResult(
    clazz: Class<T>,
    requestCode: Int,
    bundle: Bundle = Bundle()
) {
    startActivityForResult(Intent(this, clazz).putExtras(bundle), requestCode)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> AppCompatActivity.startActivity(clazz: Class<T>, bundle: Bundle) {
    val intent = Intent(this, clazz)
    intent.putExtras(bundle)
    startActivity(intent)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Fragment.startActivity(clazz: Class<T>) {
    startActivity(Intent(context, clazz))
}

/**
 * quick open new Activity with Anim
 */
fun <T : AppCompatActivity> Fragment.startActivityAnim(
    clazz: Class<T>,
    bundle: Bundle?,
    enterAnim: Int? = null,
    exitAnim: Int? = null
) {
    val intent = Intent(context, clazz)
    bundle?.let {
        intent.putExtras(bundle)
    }
    startActivity(intent)
    if (enterAnim != null && exitAnim != null) {
        activity?.overridePendingTransition(enterAnim, exitAnim)
    }
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Fragment.startActivity(clazz: Class<T>, bundle: Bundle) {
    val intent = Intent(context, clazz)
    intent.putExtras(bundle)
    startActivity(intent)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Activity.startActivity(clazz: Class<T>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Context.startActivity(clazz: Class<T>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Fragment.startActivityForResult(clazz: Class<T>, requestCode: Int) {
    val intent = Intent(context, clazz)
    startActivityForResult(intent, requestCode)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Fragment.startActivityForResult(
    clazz: Class<T>,
    bundle: Bundle,
    requestCode: Int
) {
    val intent = Intent(context, clazz)
    intent.putExtras(bundle)
    startActivityForResult(intent, requestCode)
}

/**
 * quick open new Activity
 */
fun <T : AppCompatActivity> Activity.startActivityForResult(
    clazz: Class<T>,
    bundle: Bundle,
    requestCode: Int
) {
    val intent = Intent(this, clazz)
    intent.putExtras(bundle)
    startActivityForResult(intent, requestCode)
}

/**
 * get activity context
 */
fun Context?.findActivityContext(): Activity? {
    if (this is Activity) {
        return this
    }
    var context: Context? = this
    while (context != null && context !is Activity && context is ContextWrapper) {
        context = context.baseContext
    }
    return if (context is Activity) context else null
}

fun getGlide(context: Context?): RequestManager? {

    return try {
        val mContext: Context? = if (isOnMainThread()) {
            if (isDestroyActivity(context)) {
//                YzLog.e("--------getGlide------mContext=isDestroyActivity")
                null
            } else {
                context?.findActivityContext()
            }
        } else {
            context?.applicationContext
        }
//        YzLog.e("--------getGlide------mContext=$mContext")
        if (mContext == null) null else Glide.with(mContext)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun isOnMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}

fun isDestroyActivity(context: Context?): Boolean {
    val activity = context?.findActivityContext()
    return activity == null || activity.isFinishing || activity.isDestroyed

}
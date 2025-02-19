@file:Suppress("unused")

package io.legado.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.view.menu.SubMenuBuilder
import androidx.core.view.forEach
import io.legado.app.R
import io.legado.app.constant.Theme
import java.lang.reflect.Method

@SuppressLint("RestrictedApi")
fun Menu.applyTint(context: Context, theme: Theme = Theme.Auto): Menu = this.let { menu ->
    if (menu is MenuBuilder) {
        menu.setOptionalIconsVisible(true)
    }
    val defaultTextColor = context.getCompatColor(R.color.primaryText)
    val tintColor = UIUtils.getMenuColor(context, theme)
    menu.forEach { item ->
        (item as MenuItemImpl).let { impl ->
            //overflow：展开的item
            impl.icon?.setTintMutate(
                if (impl.requiresOverflow()) defaultTextColor else tintColor
            )
        }
    }
    return menu
}

fun Menu.applyOpenTint(context: Context) {
    //展开菜单显示图标
    if (this.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
        val defaultTextColor = context.getCompatColor(R.color.primaryText)
        kotlin.runCatching {
            var method: Method =
                this.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
            method.isAccessible = true
            method.invoke(this, true)
            method = this.javaClass.getDeclaredMethod("getNonActionItems")
            val menuItems = method.invoke(this)
            if (menuItems is ArrayList<*>) {
                for (menuItem in menuItems) {
                    if (menuItem is MenuItem) {
                        menuItem.icon?.setTintMutate(defaultTextColor)
                    }
                }
            }
        }
    } else if (this.javaClass.simpleName.equals("SubMenuBuilder", ignoreCase = true)) {
        val defaultTextColor = context.getCompatColor(R.color.primaryText)
        (this as? SubMenuBuilder)?.forEach { item: MenuItem ->
            item.icon?.setTintMutate(defaultTextColor)
        }
    }
}

fun Menu.iconItemOnLongClick(id: Int, function: (view: View) -> Unit) {
    findItem(id)?.let { item ->
        item.setActionView(R.layout.view_action_button)
        item.actionView.contentDescription = item.title
        item.actionView.findViewById<ImageButton>(R.id.item).setImageDrawable(item.icon)
        item.actionView.setOnLongClickListener { function.invoke(item.actionView); true }
        item.actionView.setOnClickListener { performIdentifierAction(id, 0) }
    }
}

package com.kunpitech.shayariwala.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.kunpitech.shayariwala.ui.theme.Bg900
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.Gold300
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun BottomNavBar(
    currentDestination : NavDestination?,
    onNavigate         : (Screen) -> Unit,
    onComposeClick     : () -> Unit,
) {
    val ext            = MaterialTheme.shayariColors
    val navBarPadding  = WindowInsets.navigationBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 0.5.dp,
                color = ext.cardBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(ext.bottomNavBg)
            .padding(
                start  = 8.dp,
                end    = 8.dp,
                top    = 10.dp,
                bottom = navBarPadding.calculateBottomPadding() + 6.dp,
            ),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            // Home nav item
            NavItem(
                item               = bottomNavItems[0],
                isSelected         = currentDestination.isSelected(bottomNavItems[0].screen),
                onClick            = { onNavigate(bottomNavItems[0].screen) },
            )

            // Explore nav item
            NavItem(
                item               = bottomNavItems[1],
                isSelected         = currentDestination.isSelected(bottomNavItems[1].screen),
                onClick            = { onNavigate(bottomNavItems[1].screen) },
            )

            // Centre compose button
            ComposeButton(onClick = onComposeClick)

            // Saved
            NavItem(
                item       = bottomNavItems[2],
                isSelected = currentDestination.isSelected(bottomNavItems[2].screen),
                onClick    = { onNavigate(bottomNavItems[2].screen) },
            )

            // Profile nav item
            NavItem(
                item               = bottomNavItems[3],
                isSelected         = currentDestination.isSelected(bottomNavItems[3].screen),
                onClick            = { onNavigate(bottomNavItems[3].screen) },
            )

           /* // Placeholder 4th slot (future: Notifications)
            Box(modifier = Modifier.width(60.dp)) {
                NavItem(
                    item       = BottomNavItem(
                        screen         = Screen.Profile,          // reuse route — no-op visual
                        label          = "Saved",
                        selectedIcon   = androidx.compose.material.icons.Icons.Filled.Bookmark,
                        unselectedIcon = androidx.compose.material.icons.Icons.Outlined.BookmarkBorder,
                    ),
                    isSelected = false,
                    onClick    = { *//* TODO: Saved screen *//* },
                )
            }*/
        }
    }
}

// ─────────────────────────────────────────────────────────
// Individual nav item
// ─────────────────────────────────────────────────────────
@Composable
private fun NavItem(
    item       : BottomNavItem,
    isSelected : Boolean,
    onClick    : () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue   = if (isSelected) Gold400 else TextDisabled,
        label         = "navIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue   = if (isSelected) Gold400 else TextDisabled,
        label         = "navLabelColor",
    )
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "navIconScale",
    )

    Column(
        modifier            = Modifier
            .width(60.dp)
            .clickable(
                indication            = null,
                interactionSource     = remember { MutableInteractionSource() },
                onClick               = onClick,
            )
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Active indicator pill behind icon
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Gold400.copy(alpha = 0.12f))
                )
            }
            Icon(
                imageVector        = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint               = iconColor,
                modifier           = Modifier
                    .size(22.dp)
                    .scale(scale),
            )
        }

        Text(
            text       = item.label,
            fontSize   = 9.sp,
            color      = labelColor,
            fontFamily = DmSans,
            maxLines   = 1,
        )
    }
}

// ─────────────────────────────────────────────────────────
// Centre compose button
// ─────────────────────────────────────────────────────────
@Composable
private fun ComposeButton(onClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .width(60.dp)
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick           = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier         = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Gold400)
                .border(1.dp, Gold300.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = "✦",
                color    = Bg900,
                fontSize = 18.sp,
            )
        }
        Text(
            text       = "Likho",
            fontSize   = 9.sp,
            color      = Gold400,
            fontFamily = DmSans,
        )
    }
}

// ─────────────────────────────────────────────────────────
// Helper — check if this screen is the active route
// ─────────────────────────────────────────────────────────
private fun NavDestination?.isSelected(screen: Screen): Boolean =
    this?.hierarchy?.any { it.route == screen.route } == true
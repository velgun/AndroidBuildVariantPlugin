package com.vel.buildvariantplugin


import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.android.tools.idea.gradle.variant.view.BuildVariantUpdater
import com.android.tools.idea.projectsystem.getAndroidFacets
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import icons.StudioIcons
import javax.swing.JComponent


class SelectBuildVariantAction : ComboBoxAction(),DumbAware {

    private val actions = ArrayList<AnAction>()

    override fun shouldShowDisabledActions(): Boolean {
        return false
    }
    override fun createPopupActionGroup(button: JComponent,dataContext: DataContext): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(actions)
        return group
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = false
        var currentVariant = ""
        e.project.getAndroidFacets().firstOrNull()?.configuration?.state?.SELECTED_BUILD_VARIANT?.let {
            currentVariant = it
            e.presentation.text = it
        }
        e.presentation.icon = StudioIcons.Shell.ToolWindows.BUILD_VARIANTS
        e.presentation.disabledIcon = IconLoader.getDisabledIcon(StudioIcons.Shell.ToolWindows.BUILD_VARIANTS)
        val project = e.project
        if (project != null && !GradleSyncState.getInstance(project).isSyncInProgress) {
            val moduleManager = ModuleManager.getInstance(project)
            val androidModules = moduleManager.modules
                .map { GradleAndroidModel.get(it) }
                .filter { it?.moduleName != null }
                .map { it!! }
                .distinct()
            getBuildTypes(androidModules.firstOrNull()?.variantNames,project,currentVariant)
            e.presentation.isEnabled = actions.isNotEmpty()
        }else{
            actions.clear()
            e.presentation.isEnabled = false
        }
        super.update(e)
    }

    private fun getBuildTypes(buildVariants: Collection<String>?, project: Project, currentVariant: String){
        actions.clear()
        buildVariants?.forEach { variant ->
            actions.add(object : AnAction(variant) {
                override fun update(e: AnActionEvent) {
                    e.presentation.text = variant
                    if(currentVariant == variant){
                        e.presentation.icon = AllIcons.Actions.Checked_selected
                    }else{
                        e.presentation.icon = null
                    }
                    super.update(e)
                }

                override fun actionPerformed(e: AnActionEvent) {
                    if(e.presentation.text != currentVariant) {
                        project.getAndroidFacets().firstOrNull()?.mainModule?.let {
                            BuildVariantUpdater.getInstance(project).run {
                                updateSelectedBuildVariant(it, variant)
                            }
                        }
                    }
                }
            })
        }
    }
}


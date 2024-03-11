package com.vel.buildvariantplugin


import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.android.tools.idea.gradle.variant.view.BuildVariantUpdater
import com.android.tools.idea.projectsystem.getAndroidFacets
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import javax.swing.JComponent


class SelectBuildVariantAction : ComboBoxAction() {

    private val actions = ArrayList<AnAction>()

    override fun shouldShowDisabledActions(): Boolean {
        return true
    }

    override fun createPopupActionGroup(button: JComponent,dataContext: DataContext): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(actions)
        return group

    }

    override fun update(e: AnActionEvent) {
        var currentVariant = ""
        e.project.getAndroidFacets().firstOrNull()?.configuration?.state?.SELECTED_BUILD_VARIANT?.let {
            currentVariant = it
            e.presentation.text = it
        }

        super.update(e)
        val project = e.project
        if (project != null) {
            val moduleManager = ModuleManager.getInstance(project)
            val androidModules = moduleManager.modules
                .map { GradleAndroidModel.get(it) }
                .filter { it?.moduleName != null }
                .map { it!! }
                .distinct()
            getBuildTypes(androidModules.firstOrNull()?.variantNames,project,currentVariant)
        }
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


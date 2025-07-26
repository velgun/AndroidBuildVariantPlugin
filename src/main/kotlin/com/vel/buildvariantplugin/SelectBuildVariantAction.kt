package com.vel.buildvariantplugin


import com.android.tools.idea.gradle.model.IdeVariantBuildInformation
import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.android.tools.idea.gradle.variant.view.BuildVariantUpdater
import com.android.tools.idea.projectsystem.getAndroidFacets
import com.android.tools.idea.run.AndroidRunConfiguration
import com.intellij.execution.RunManager
import com.intellij.execution.RunManagerEx
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import icons.StudioIcons
import org.jetbrains.android.facet.AndroidFacet
import javax.swing.JComponent


class SelectBuildVariantAction : ComboBoxAction(), DumbAware {

    private val actions = ArrayList<AnAction>()

    override fun shouldShowDisabledActions(): Boolean {
        return false
    }

    override fun createPopupActionGroup(button: JComponent, dataContext: DataContext): DefaultActionGroup {
        val group = DefaultActionGroup()
        group.addAll(actions)
        return group
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = false
        e.presentation.icon = StudioIcons.Shell.ToolWindows.BUILD_VARIANTS
        e.presentation.disabledIcon = IconLoader.getDisabledIcon(StudioIcons.Shell.ToolWindows.BUILD_VARIANTS)
        var currentVariant = ""
        val project = e.project
        if (project != null && !GradleSyncState.getInstance(project).isSyncInProgress) {
            val runManager = RunManager.getInstance(project) as RunManagerEx
            val selectedConfiguration = runManager.selectedConfiguration
            val runConfiguration = selectedConfiguration?.configuration
            if (runConfiguration is AndroidRunConfiguration) {
                val module = runConfiguration.modules.firstOrNull()
                val androidFacet = module?.let { AndroidFacet.getInstance(it) }
                val androidModule = module?.let { GradleAndroidModel.get(it) }
                if (androidFacet != null && androidModule != null) {
                    androidFacet.configuration.state.SELECTED_BUILD_VARIANT?.let {
                        currentVariant = it
                        e.presentation.text = it
                    }
                    getBuildTypes(androidModule.androidProject.variantsBuildInformation, project, currentVariant, module)
                    e.presentation.isEnabled = actions.isNotEmpty()
                } else {
                    actions.clear()
                }
            } else {
                actions.clear()
            }
        } else {
            actions.clear()
        }
        super.update(e)
    }

    private fun getBuildTypes(
        buildVariants: Collection<IdeVariantBuildInformation>,
        project: Project,
        currentVariant: String,
        module: Module
    ) {
        actions.clear()
        buildVariants.forEach { variant ->
            actions.add(object : AnAction(variant.variantName) {
                override fun update(e: AnActionEvent) {
                    e.presentation.text = variant.variantName
                    if (currentVariant == variant.variantName) {
                        e.presentation.icon = AllIcons.Actions.Checked_selected
                    } else {
                        e.presentation.icon = null
                    }
                    super.update(e)
                }

                override fun actionPerformed(e: AnActionEvent) {
                    if (e.presentation.text != currentVariant) {
                        project.getAndroidFacets().firstOrNull()?.module?.let {
                            BuildVariantUpdater.getInstance(project).run {
                                updateSelectedBuildVariant(module, variant.variantName)
                            }
                        }
                    }
                }
            })
        }
    }
}


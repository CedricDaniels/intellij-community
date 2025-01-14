// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.wm.impl

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.openapi.wm.ToolWindowManager.Companion.getInstance
import com.intellij.openapi.wm.impl.ToolwindowSidebarPositionProvider.Companion.isRightPosition
import com.intellij.ui.components.OnOffButton
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Box
import javax.swing.JLabel
import javax.swing.JPanel

class IdeLeftToolbar internal constructor() : JPanel() {
  lateinit var pane: ToolWindowsPane
  private val squareButtonPane: JPanel
  private val extendedPane: JPanel
  private val moreButton: MoreSquareStripeButton
  private val extendedBag: GridBag
  private val horizontalGlue = Box.createHorizontalGlue()

  init {
    initSideBar()

    extendedPane = initExtendedPane()
    extendedBag = GridBag()
    add(extendedPane, if (isRightPosition()) BorderLayout.WEST else BorderLayout.EAST)

    squareButtonPane = initMainPane()
    add(squareButtonPane, BorderLayout.CENTER)

    moreButton = MoreSquareStripeButton(this)
  }

  private fun initSideBar() {
    layout = BorderLayout()
    border = JBUI.Borders.empty()
    isOpaque = true
  }

  fun addStripeButton(project: Project, button: StripeButton) {
    val stickySquareButton = SquareStripeButton(button)
    if (PropertiesComponent.getInstance(project).getValues(STICKY_TW) == null) {
      ToolWindowSidebarProvider.getInstance().defaultToolwindows(project).forEach {
        saveTWid(project, it)
      }
    }

    if (isTWSticky(project, stickySquareButton)) {
      rebuildOrderedSquareButtons(project)
    }

    addButtonOnExtendedPane(project, button, stickySquareButton)
  }

  private fun addButtonOnExtendedPane(project: Project, button: StripeButton, stickySquareButton: SquareStripeButton) {
    extendedBag.nextLine()
    extendedPane.apply {
      remove(horizontalGlue)
      add(SquareStripeButton(button), extendedBag.next())
      add(JLabel(button.text).apply { border = JBUI.Borders.emptyRight(50) }, extendedBag.next().anchor(GridBagConstraints.WEST))
      add(createOnOffButton(project, stickySquareButton), extendedBag.next().coverLine())
      add(horizontalGlue, extendedBag.nextLine().next().weighty(1.0).fillCell().coverLine())
    }
  }

  private fun createOnOffButton(project: Project, stickySquareButton: SquareStripeButton) =
    OnOffButton().also {
      it.model.isSelected = isTWSticky(project, stickySquareButton)

      it.addActionListener(object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
          if (it.isSelected) {
            addStickySquareStripeButton(project, stickySquareButton)
          }
          else {
            removeSquareStripeButton(project, stickySquareButton)
          }
          squareButtonPane.revalidate()
        }
      })
    }

  private fun rebuildOrderedSquareButtons(project: Project) {
    squareButtonPane.removeAll()
    PropertiesComponent.getInstance(project).getValues(STICKY_TW).orEmpty().forEach { sticky ->
      val toolWindow = getInstance(project).getToolWindow(sticky) as ToolWindowImpl? ?: return@forEach
      squareButtonPane.add(SquareStripeButton(StripeButton(pane, toolWindow)))
    }
    squareButtonPane.add(moreButton)
  }

  private fun addStickySquareStripeButton(project: Project, stickySquareButton: SquareStripeButton) {
    saveTWid(project, stickySquareButton.toolWindow.id)
    rebuildOrderedSquareButtons(project)
  }

  private fun removeSquareStripeButton(project: Project, stickySquareButton: SquareStripeButton) {
    unsetTWid(project, stickySquareButton)
    rebuildOrderedSquareButtons(project)
  }

  fun openExtendedToolwindowPane(show: Boolean) {
    extendedPane.isVisible = show
  }

  companion object {
    private const val STICKY_TW = "STICKY_TW"

    private fun saveTWid(project: Project, id: String) {
      val stickies = PropertiesComponent.getInstance(project).getValues(STICKY_TW).orEmpty() as Array<String>
      if (!stickies.contains(id)) {
        PropertiesComponent.getInstance(project).setValues(STICKY_TW, stickies.plus(id))
      }
    }

    private fun isTWSticky(project: Project, stickySquareButton: SquareStripeButton) =
      PropertiesComponent.getInstance(project).getValues(STICKY_TW)?.contains(stickySquareButton.toolWindow.id) ?: false

    private fun unsetTWid(project: Project, stickySquareButton: SquareStripeButton) {
      PropertiesComponent.getInstance(project).setValues(
        STICKY_TW,
        (PropertiesComponent.getInstance(project).getValues(STICKY_TW).orEmpty()).toMutableList().apply {
          remove(stickySquareButton.toolWindow.id)
        }.toTypedArray().ifEmpty { null })
    }

    private fun initMainPane() = JPanel(VerticalFlowLayout(0, 0))
      .apply {
        border = JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 1,
                                         if (isRightPosition()) 1 else 0, 0, if (isRightPosition()) 0 else 1)
      }

    private fun initExtendedPane() = JPanel()
      .apply {
        layout = GridBagLayout()
        isVisible = false
        background = JBUI.CurrentTheme.BigPopup.searchFieldBackground()
        border = JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 1,
                                         if (isRightPosition()) 1 else 0, 0, if (isRightPosition()) 0 else 1)
      }
  }
}
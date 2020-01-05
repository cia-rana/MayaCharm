package debugattach

import settings.ApplicationSettings
import com.intellij.execution.process.ProcessInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.UserDataHolder
import com.intellij.xdebugger.attach.*
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import javax.swing.Icon


class MayaAttachDebuggerProvider : XAttachDebuggerProvider {
    override fun getPresentationGroup(): XAttachPresentationGroup<ProcessInfo> {
        return MayaAttachGroup.INSTANCE
    }

    override fun getAvailableDebuggers(project: Project, attachHost: XAttachHost, processInfo: ProcessInfo, userData: UserDataHolder): MutableList<XAttachDebugger> {
        val sdks = ApplicationSettings.INSTANCE.mayaSdkMapping.values

        if (!sdks.any { processInfo.commandLine.contains(it.mayaPath) }) {
            return mutableListOf()
        }

        val currentSdk = sdks.firstOrNull { processInfo.commandLine.contains(it.mayaPath) } ?: return mutableListOf()
        return mutableListOf(MayaAttachDebugger(PythonSdkUtil.findSdkByPath(currentSdk.mayaPyPath)!!))
    }

    override fun isAttachHostApplicable(attachHost: XAttachHost): Boolean {
        for (info in attachHost.processList) {
            val path = info.executableCannonicalPath
            if (path.isPresent) {
                println(path.get())
            }
        }
        return true // TODO properly check this
    }

}

private class MayaAttachDebugger(sdk: Sdk) : XAttachDebugger {
    private val mySdkHome: String? = sdk.homePath
    private val myName: String = "${PythonSdkType.getInstance().getVersionString(sdk)} ($mySdkHome)"

    override fun getDebuggerDisplayName(): String {
        return myName
    }

    override fun attachDebugSession(project: Project, attachHost: XAttachHost, processInfo: ProcessInfo) {
        val runner = MayaAttachToProcessDebugRunner(project, processInfo.pid, mySdkHome)
        runner.launch()
    }
}

private class MayaAttachGroup : XAttachProcessPresentationGroup {
    companion object {
        val INSTANCE = MayaAttachGroup()
    }

    override fun getItemDisplayText(project: Project, processInfo: ProcessInfo, userData: UserDataHolder): String {
        return processInfo.executableDisplayName
    }

    override fun getProcessDisplayText(project: Project, info: ProcessInfo, userData: UserDataHolder): String {
        return getItemDisplayText(project, info, userData)
    }

    override fun getItemIcon(project: Project, processInfo: ProcessInfo, userData: UserDataHolder): Icon {
        return IconLoader.getIcon("/icons/MayaCharm_ToolWindow.png")
    }

    override fun getProcessIcon(project: Project, info: ProcessInfo, userData: UserDataHolder): Icon {
        return getItemIcon(project, info, userData)
    }

    override fun getGroupName(): String {
        return "Maya"
    }

    override fun getOrder(): Int {
        return -100
    }
}

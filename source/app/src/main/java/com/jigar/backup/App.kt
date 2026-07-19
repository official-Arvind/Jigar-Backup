package com.jigar.backup

import android.app.Application
import com.jigar.backup.data.AppRepository
import com.jigar.backup.data.BackupConfigRepository
import com.jigar.backup.data.BackupProcessRepository
import com.jigar.backup.data.CallLogRepository
import com.jigar.backup.data.ContactRepository
import com.jigar.backup.data.FileRepository
import com.jigar.backup.data.GitHubReleaseRepository
import com.jigar.backup.data.MessageRepository
import com.jigar.backup.data.NetworkRepository
import com.jigar.backup.data.rustic.RusticAppSourcePlanner
import com.jigar.backup.data.rustic.RusticBackupCoordinator
import com.jigar.backup.data.rustic.RusticBackupGateway
import com.jigar.backup.data.rustic.RusticBackupSelectionProvider
import com.jigar.backup.data.rustic.RusticBackupSourceCollector
import com.jigar.backup.data.rustic.RusticStructuredDataSerializer
import com.jigar.backup.feature.backup.BackupConfigViewModel
import com.jigar.backup.feature.backup.BackupProcessViewModel
import com.jigar.backup.feature.backup.BackupSetupViewModel
import com.jigar.backup.feature.backup.apps.AppsViewModel
import com.jigar.backup.feature.backup.call_logs.CallLogsViewModel
import com.jigar.backup.feature.backup.contacts.ContactsViewModel
import com.jigar.backup.feature.backup.messages.MessagesViewModel
import com.jigar.backup.feature.backup.networks.NetworksViewModel
import com.jigar.backup.feature.backup.rustic.RusticBackupProcessViewModel
import com.jigar.backup.feature.dashboard.DashboardViewModel
import com.jigar.backup.feature.update.UpdatesViewModel
import com.jigar.backup.service.util.BackupAppsHelper
import com.jigar.backup.service.util.BackupCallLogsHelper
import com.jigar.backup.service.util.BackupContactsHelper
import com.jigar.backup.service.util.BackupMessagesHelper
import com.jigar.backup.service.util.BackupNetworksHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {
    companion object {
        lateinit var application: Application
    }

    private val appModule = module {
        singleOf(::BackupConfigRepository) bind BackupConfigRepository::class
        singleOf(::AppRepository) bind AppRepository::class
        singleOf(::FileRepository) bind FileRepository::class
        singleOf(::NetworkRepository) bind NetworkRepository::class
        singleOf(::ContactRepository) bind ContactRepository::class
        singleOf(::CallLogRepository) bind CallLogRepository::class
        singleOf(::MessageRepository) bind MessageRepository::class
        singleOf(::BackupProcessRepository) bind BackupProcessRepository::class
        singleOf(::GitHubReleaseRepository) bind GitHubReleaseRepository::class
        singleOf(::BackupAppsHelper) bind BackupAppsHelper::class
        singleOf(::BackupNetworksHelper) bind BackupNetworksHelper::class
        singleOf(::BackupContactsHelper) bind BackupContactsHelper::class
        singleOf(::BackupCallLogsHelper) bind BackupCallLogsHelper::class
        singleOf(::BackupMessagesHelper) bind BackupMessagesHelper::class
        singleOf(::RusticAppSourcePlanner)
        singleOf(::RusticStructuredDataSerializer)
        singleOf(::RusticBackupGateway)
        singleOf(::RusticBackupSelectionProvider)
        singleOf(::RusticBackupSourceCollector)
        singleOf(::RusticBackupCoordinator)

        viewModelOf(::DashboardViewModel)
        viewModelOf(::BackupSetupViewModel)
        viewModelOf(::BackupProcessViewModel)
        viewModelOf(::RusticBackupProcessViewModel)
        viewModelOf(::BackupConfigViewModel)
        viewModelOf(::AppsViewModel)
        viewModelOf(::NetworksViewModel)
        viewModelOf(::ContactsViewModel)
        viewModelOf(::CallLogsViewModel)
        viewModelOf(::MessagesViewModel)
        viewModelOf(::UpdatesViewModel)
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        startKoin {
            androidLogger()
            androidContext(application)
            modules(appModule)
        }
    }
}

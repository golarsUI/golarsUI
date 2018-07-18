import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { ImportComponent } from './import/import.component';
import { UsersComponent } from './users/users.component';
import { AppComponent } from './app.component';
import { UserComponent } from './user/user.component';
import { ChangepasswordComponent } from './changepassword/changepassword.component';
import { SettingsComponent } from './settings/settings.component';
import { ConfigurationComponent } from './configuration/configuration.component';
import { ResetpasswordComponent } from './resetpassword/resetpassword.component';



const appRoutes: Routes = [
    { path: '', component: HomeComponent},
    { path: 'login', component: LoginComponent },
    { path: 'import', component: ImportComponent },
    { path: 'users', component: UsersComponent },
    { path: 'settings', component: SettingsComponent },
    { path: 'newuser', component: UserComponent },
    { path: 'changepassword', component: ChangepasswordComponent },
    { path: 'resetpassword', component: ResetpasswordComponent  },
    { path: 'configuration', component: ConfigurationComponent },
    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes);
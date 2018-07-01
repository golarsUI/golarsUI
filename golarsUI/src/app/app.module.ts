import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule }    from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { routing } from './app.routing';
import { AuthenticationService } from './services/authentication.service';
import { ImportComponent } from './import/import.component';
import { UsersComponent } from './users/users.component';
import {AngularSplitModule} from 'angular-split';
import {TreeModule} from 'primeng/tree';
import { LeftnavComponent } from './leftnav/leftnav.component';
import { FolderService } from './services/folder.service';
import { CommonService } from './services/common.service';
import { MiddlepaneComponent } from './middlepane/middlepane.component';
import { RightpanelComponent } from './rightpanel/rightpanel.component';
import {TableModule} from 'primeng/table';
import { UserService } from './services/user.service';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import {CalendarModule} from 'primeng/calendar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {DropdownModule} from 'primeng/dropdown';
import {CheckboxModule} from 'primeng/checkbox';
import {ContextMenuModule} from 'primeng/contextmenu';
import {FileUploadModule} from 'primeng/fileupload';
import { ImportService } from './services/import.service';

@NgModule({
  declarations: [

    AppComponent,
    HomeComponent,
    LoginComponent,
    ImportComponent,
    UsersComponent,
    LeftnavComponent,
    MiddlepaneComponent,
    RightpanelComponent,
    
  ],
  imports: [
    routing,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AngularSplitModule,
    TreeModule,
    TableModule,
    BrowserAnimationsModule,
    CalendarModule,
    DropdownModule,
    CheckboxModule,
    ContextMenuModule,
    FileUploadModule
 
    
    
  ],
  
  providers: [ 
    AuthenticationService,
    FolderService,
    CommonService,
    UserService,
    ImportService,
    {provide: LocationStrategy, useClass: HashLocationStrategy},
  ],
  
  bootstrap: [AppComponent]
})
export class AppModule { }

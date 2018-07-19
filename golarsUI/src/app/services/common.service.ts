import { Subject } from "rxjs";
import { GolarsConstants } from "../constants/golarsconstants";
import { ImportFieldValues } from "../import/import.mapping";


export class CommonService {

  private notifySub = new Subject<any>();
  /**
   * Observable string streams
   */
  notifyObservable$ = this.notifySub.asObservable();

  constructor(){}

  public notify(data: any) {
    if (data) {
      this.notifySub.next(data);
    }
  }
  getUserName(){
    if(localStorage.getItem("username")!==null)
      return localStorage.getItem("username");
  else
      return "";
  }
  getEditUser(){
    if(localStorage.getItem("editUser")!==null)
      return localStorage.getItem("editUser");
  else
      return null;
    
  }
  removeEditUser(){
    localStorage.removeItem("editUser");
  }
  isAdmin(){
    if(localStorage.getItem("admin")!==null)
    if(localStorage.getItem("admin")=="true")
      return true;
  else
      return false;
  }
  getTableAdminPreferences(){
    if(localStorage.getItem("adminTableColumns")!==null && localStorage.getItem("adminTableColumns")!=="")
    return localStorage.getItem("adminTableColumns");

      return GolarsConstants.DEFAULT_TABLE_PREFERENCES;
  }
  getTableNonAdminPreferences(){
    if(localStorage.getItem("nonAdminTableColumns")!==null && localStorage.getItem("nonAdminTableColumns")!=="")
    return localStorage.getItem("nonAdminTableColumns");

      return GolarsConstants.DEFAULT_TABLE_PREFERENCES;
  }
  updatePreferences(data){
    for(var i=0;i<data.length;i++){
      localStorage.setItem(data[i].key, data[i].value);  
    } 
  }
  getStateProgramPreferences(){
    if(localStorage.getItem("stateProgramPreferences")!==null && localStorage.getItem("stateProgramPreferences")!=="")
    return JSON.parse(localStorage.getItem("stateProgramPreferences"));

      return ImportFieldValues.stateProgramMapping;
  }
  getDocumentTypePreferences(){
    if(localStorage.getItem("docTypePreferences")!==null && localStorage.getItem("docTypePreferences")!=="")
    return JSON.parse(localStorage.getItem("docTypePreferences"));

      return ImportFieldValues.stateProgramMappingForDocumentType;
  }
  getScopeOfWorkPreferences(){
    if(localStorage.getItem("scopeOfWorkPreferences")!==null && localStorage.getItem("scopeOfWorkPreferences")!=="")
    return JSON.parse(localStorage.getItem("scopeOfWorkPreferences"));

      return ImportFieldValues.stateProgramMappingForScopeOfWork;
  }
  getLoginContentURL(){
    if(localStorage.getItem("loginContentURL")!==null && localStorage.getItem("loginContentURL")!=="")
    return localStorage.getItem("loginContentURL");

      return GolarsConstants.DEFAULT_LOGIN_CONTENT_URL;

  }
 
}

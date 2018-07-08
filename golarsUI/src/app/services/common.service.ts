import { Subject } from "rxjs";
import { GolarsConstants } from "../constants/golarsconstants";


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
    for(var i=0;i<=data.length;i++){
      localStorage.setItem(data[0].key, data[0].value);  
    } 
  }
  
}

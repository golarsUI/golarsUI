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
  getTablePreferences(){
    if(localStorage.getItem("tablePrefernces")!==null && localStorage.getItem("tablePrefernces")!=="")
    return localStorage.getItem("tablePrefernces");

      return GolarsConstants.DEFAULT_TABLE_PREFERENCES;
  }
}

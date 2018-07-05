import { Subject } from "rxjs";


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
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { URLConstants } from '../constants/urlconstants';
import { GolarsConstants } from '../constants/golarsconstants';

@Injectable()
export class FolderService {

  constructor(private http: HttpClient) { }

  fetchFolders(id: string,parentid: string, docRequired: boolean, username: string,isadmin:boolean) {
      return this.http.get<any>(URLConstants.FOLDER_URL,this.getOptions(id,parentid,docRequired,username,isadmin))
          .map(folder => {
              
              return folder;
          });
  }
  fetchTablePreferences(isadmin:boolean) {
    return this.http.get<any>(URLConstants.FOLDER_TABLE_PREFERENCES,this.getPreferencesOptions(isadmin))
        .map(folder => {
            
            return folder;
        });
}
  fetchDocumentDetails(documentName) {
    return this.http.get<any>(URLConstants.DOCUMENT_DETAILS_URL,this.getDocOptions(documentName))
        .map(docProperties => {
            
            return docProperties;
        });
}
createFolder(foldername: string, parentFolderId: string,isFolder:boolean,username:string) {
  return this.http.post<any>(URLConstants.FOLDER_URL, { label: foldername, parentid: parentFolderId,folder:isFolder,username:username })
      .map(folder => {
          // login successful if there's a token in the response
         

          return folder;
      });
}
deleteFolder(id: string,parentId:String,username:String,isAdmin:boolean) {
  return this.http.delete<any>(URLConstants.FOLDER_URL,this.getDeleteFolderOptions(id,parentId,username,isAdmin))
      .map(folder => {
          
          return folder;
      });
}

  private getOptions(id,parentId, docRequired,username,isadmin) {
    return {
      params: new HttpParams().set(GolarsConstants.FOLDER_ID,id).set(GolarsConstants.PARENT_ID,parentId).set(GolarsConstants.DOCUMENTS_REQUIRED,docRequired)
      .set(GolarsConstants.USERNAME,username).set(GolarsConstants.ISADMIN,isadmin)
    };
  }
  private getDocOptions(documentName) {
    return {
      params: new HttpParams().set(GolarsConstants.DOCUMENT_NAME,documentName)
    };
  }
  private getDeleteFolderOptions(id,parentId,username,isAdmin) {
    return {
      params: new HttpParams().set(GolarsConstants.FOLDER_ID,id).set(GolarsConstants.PARENT_ID,parentId).set(GolarsConstants.USERNAME,username).set(GolarsConstants.ISADMIN,isAdmin)
    };
  }
  getPreferencesOptions(isAdmin){
    return {
      params: new HttpParams().set(GolarsConstants.ISADMIN,isAdmin)
    };
  }

}

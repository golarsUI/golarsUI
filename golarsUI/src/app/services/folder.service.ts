import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { URLConstants } from '../constants/urlconstants';
import { GolarsConstants } from '../constants/golarsconstants';

@Injectable()
export class FolderService {

  constructor(private http: HttpClient) { }

  fetchFolders(id: string,parentid: string, docRequired: boolean) {
      return this.http.get<any>(URLConstants.FOLDER_URL,this.getOptions(id,parentid,docRequired))
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
deleteFolder(id: string,parentId:String) {
  return this.http.delete<any>(URLConstants.FOLDER_URL,this.getDeleteFolderOptions(id,parentId))
      .map(folder => {
          
          return folder;
      });
}

  private getOptions(id,parentId, docRequired) {
    return {
      params: new HttpParams().set(GolarsConstants.FOLDER_ID,id).set(GolarsConstants.PARENT_ID,parentId).set(GolarsConstants.DOCUMENTS_REQUIRED,docRequired)
    };
  }
  private getDocOptions(documentName) {
    return {
      params: new HttpParams().set(GolarsConstants.DOCUMENT_NAME,documentName)
    };
  }
  private getDeleteFolderOptions(id,parentId) {
    return {
      params: new HttpParams().set(GolarsConstants.FOLDER_ID,id).set(GolarsConstants.PARENT_ID,parentId)
    };
  }

}

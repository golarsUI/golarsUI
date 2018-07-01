import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { URLConstants } from '../constants/urlconstants';
import { GolarsConstants } from '../constants/golarsconstants';

@Injectable()
export class FolderService {

  constructor(private http: HttpClient) { }

  fetchFolders(id: string, docRequired: boolean) {
      return this.http.get<any>(URLConstants.FOLDER_URL,this.getOptions(id,docRequired))
          .map(folder => {
              
              return folder;
          });
  }
  fetchDocumentDetails(id: string) {
    return this.http.get<any>(URLConstants.DOCUMENT_DETAILS_URL,this.getDocOptions(id))
        .map(docProperties => {
            
            return docProperties;
        });
}
createFolder(foldername: string, parentFolderId: string) {
  return this.http.post<any>(URLConstants.FOLDER_URL, { label: foldername, parentid: parentFolderId })
      .map(folder => {
          // login successful if there's a token in the response
         

          return folder;
      });
}
deleteFolder(id: string) {
  return this.http.delete<any>(URLConstants.FOLDER_URL,this.getDocOptions(id))
      .map(folder => {
          
          return folder;
      });
}

  private getOptions(id, docRequired) {
    return {
      params: new HttpParams().set(GolarsConstants.FOLDER_ID,id).set(GolarsConstants.DOCUMENTS_REQUIRED,docRequired)
    };
  }
  private getDocOptions(id) {
    return {
      params: new HttpParams().set(GolarsConstants.DOCUMENTS_ID,id)
    };
  }

}

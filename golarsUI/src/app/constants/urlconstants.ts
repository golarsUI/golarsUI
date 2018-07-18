import { environment } from "../../environments/environment";

export class URLConstants {
    public static LOGIN_URL = environment.server+'/golars/rest/login';
    public static FOLDER_URL = environment.server+'/golars/rest/folders';
    public static SEARCH_URL = environment.server+'/golars/rest/folders/search';
    public static DOCUMENT_DETAILS_URL = environment.server+'/golars/rest/folders/documentdetails';
    public static USERS_URL = environment.server+'/golars/rest/users';
    public static USER_CHANGE_PASSWORD_URL = environment.server+'/golars/rest/users/changepassword';
    public static IMPORT_DOC_URL = environment.server+'/golars/rest/import';
    public static FOLDER_TABLE_PREFERENCES = environment.server+'/golars/rest/folders/preferences';
    public static FORGOT_PASSWORD_URL = environment.server+'/golars/rest/forgotpassword';

}
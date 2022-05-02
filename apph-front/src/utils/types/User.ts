export interface IUser {
  firstname: string;
  lastname: string;
  login: string;
}

export interface IEditedUser {
  firstname?: string;
  lastname?: string;
  login?: string;
  password?: string;
}

export interface IUser {
  firstname: string;
  lastname: string;
  login: string;
}

export interface IUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
}

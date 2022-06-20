export interface IUser {
  firstname: string;
  lastname: string;
  login: string;
  isAdmin?: boolean;
}

export interface IUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
}

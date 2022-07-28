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

export const emailValidator =
  /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

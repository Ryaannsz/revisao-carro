import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { HttpClient } from "@angular/common/http";
import { AppConstants } from "../constants/app.constants";
import { AuthService } from "./auth.service";
import { FormBuilder } from "@angular/forms";
import { User } from "../models/user.model";
import { tap } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class LoginService extends BaseService{
    constructor(http: HttpClient,
        private authService: AuthService
    ){
        super(http);
        this.path=AppConstants.LOGIN_URL;
    }


login(data: FormBuilder) {
  return this.post< {token: string}>(data).pipe(
    tap((response: {token: string}) => {
      this.authService.setToken(response.token);
    })
  );
}
}
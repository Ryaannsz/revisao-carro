import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";


@Injectable({
    providedIn: 'root'
})

export class RegistroService extends BaseService{
    constructor(http: HttpClient){
        super(http);
        this.path=AppConstants.REGISTRO_URL;
    }
}
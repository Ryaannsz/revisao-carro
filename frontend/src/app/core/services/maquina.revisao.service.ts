import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { HttpClient } from "@angular/common/http";
import { AppConstants } from "../constants/app.constants";

@Injectable({
    providedIn: 'root'
})

export class RevisaoMaquinaService extends BaseService {

    constructor(http: HttpClient) {
        super(http);
        this.path = AppConstants.REVISAO_MAQUINA_URL;
    }

}
import { HttpClient } from "@angular/common/http";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Revisao } from "../models/revisao.model";

@Injectable({
    providedIn: 'root'
})

export class RevisaoService extends BaseService{

    constructor(http: HttpClient) {
        super(http);
        this.path=AppConstants.REVISAO_URL
    }

    revisaoCarro(id: number): Observable<Revisao[]>{
        return this.getListWithId(id);
    }

}
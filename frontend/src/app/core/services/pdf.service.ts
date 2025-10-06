import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BaseService } from "./base.service";
import { AppConstants } from "../constants/app.constants";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class PdfService extends BaseService{

    constructor(http: HttpClient){
        super(http);
        this.path=AppConstants.PDF_URL
    }

    makeCarOptionReport(carIds: number[], months = 6, mode: 'sum'|'compare' = 'compare'): Observable<Blob>{
        let params = new HttpParams().set('carIds', carIds.join(','));
        if (months !== undefined && months !== null) params = params.set('months', String(months));
        if (mode) params = params.set('mode', mode);
        const url = `${this.path}/dashboard/caroption/report.pdf`
        return this.http.get(url, {params, responseType: 'blob' as 'blob'})
    }
}
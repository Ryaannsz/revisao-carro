import { platformBrowser } from '@angular/platform-browser';
import { AppModule } from './app/app.module';
import { environment } from './app/enviroment/enviroment';
import { worker } from './mocks/browser';

function setupMocks() {
  if (environment.useMocks) {
    return import('./mocks/browser').then(({ worker }) => {
      return worker.start({
        serviceWorker: {
          url: '/mockServiceWorker.js',
        },
      });
    });
  }
  return Promise.resolve();
}

setupMocks().then(() => {
  platformBrowser().bootstrapModule(AppModule, {
    ngZoneEventCoalescing: true,
  }).catch(err => console.error(err));
});


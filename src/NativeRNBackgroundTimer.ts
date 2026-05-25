import type {TurboModule} from 'react-native/Libraries/TurboModule/RCTExport';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  start(delay: number): Promise<boolean>;
  stop(): Promise<boolean>;
  setTimeout(timeoutId: number, timeout: number): Promise<boolean>;
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.get<Spec>('RNBackgroundTimer');

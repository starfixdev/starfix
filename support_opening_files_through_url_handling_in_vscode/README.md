# Support opening files through URL handling in Vscode

## File Chages Required:

### `src/vs/platform/windows/common/windows.ts`
 `@@ export interface IWindowsService {`

[+] `openFileForURI(filePath: String): TPromise<void>;`

*****************************************************************
### src/vs/platform/windows/common/windowsIpc.ts
`@@ export interface IWindowsChannel extends IChannel {
`

[+] `call(command: 'openFileForURI', arg: string): TPromise<void>;`

-----------------------------------------------------------------------

`@@ export class WindowsChannel implements IWindowsChannel {`

[+] `case 'openFileForURI': return this.service.openFileForURI(arg);`

---------------------------------------------------------------------------
`@@ export class WindowsChannelClient implements IWindowsService {`

`openFileForURI(filePath: string): TPromise<void> {
		return this.channel.call('openFileForURI', filePath);
	}`
  
  --------------------------------------------------------------------------
  
  ### src/vs/platform/windows/electron-main/windowsService.ts
  
  `@@ import { TPromise } from 'vs/base/common/winjs.base';`
  
  [+] `import Event, { chain } from 'vs/base/common/event';`
  
  -----------------------------------------------------------------------------
  `@@ export class WindowsService implements IWindowsService {`
  
  [+]
  ```constructor(
  @IWindowsMainService private windowsMainService: IWindowsMainService,
  @IEnvironmentService private environmentService: IEnvironmentService,
  @IURLService private urlService: IURLService
	) {
		chain(urlService.onOpenURL)
			.filter(uri => uri.authority === 'file')
			.on(uri => this.openFileForURI(uri.path), this);
	}
  
  
  
  openFileForURI(filePath: string): TPromise<void> {
		if (!filePath || !filePath.length) {
			return TPromise.as(null);
		}

		var envServiceArgs = this.environmentService.args;
		envServiceArgs.goto = true;

		this.windowsMainService.open({ cli: envServiceArgs, pathsToOpen: [filePath] });
		return TPromise.as(null);
	}
  
  

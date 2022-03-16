# Frontend

## Setup your dev environment
1. Install [Node.js 14 LTS](https://nodejs.org/en/download/)
2. Install [Yarn](https://yarnpkg.com/getting-started/install): `npm install -g yarn`

## Recommended IDE Setup

- [VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=johnsoncodehk.volar)
- Disable the Vetur extension, if enabled
- For optimal developer experience, open only the frontend folder in a VSCode window and install the extensions recommended in `.vscode/extensions.json`

## Important commands
```sh
# Install dependencies
yarn

# Run development server with HMR
yarn dev
```


## Type Support For `.vue` Imports in TS

Since TypeScript cannot handle type information for `.vue` imports, they are shimmed to be a generic Vue component type by default. In most cases this is fine if you don't really care about component prop types outside of templates. However, if you wish to get actual prop types in `.vue` imports (for example to get props validation when using manual `h(...)` calls), you can enable Volar's `.vue` type support plugin by running `Volar: Switch TS Plugin on/off` from VSCode command palette.

## End to End tests
All End to End tests are defined in `cypress/integration/end-to-end/`
To run cypress tests on local machine cd into /frontend and run `yarn cypress open`

To monitor Test Runs in Cypress Dashboard:
1. Visit https://dashboard.cypress.io/projects/gy8kfo (an admin need to grant you access)
2. Open a terminal in the frontend folder (the folder of the file you are currently reading)
3. run `yarn cypress run --record --key <KEY_FROM_DASHBOARD>` 
4. Now the Test Runs will be monitored to the Dashboard
5. End-to-End Tests can also be launched from CI Pipeline see `.gitlab-ci.yaml`

## Useful links
* Cypress docs: https://docs.cypress.io/guides/
* Nuxt.js docs: https://v3.nuxtjs.org/getting-started/introduction
* Vue.js 3 docs: https://vuejs.org/guide/introduction.html
* Yarn docs: https://yarnpkg.com/getting-started
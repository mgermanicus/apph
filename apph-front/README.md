# What to do first

## Run `npm install`

In your apph-front folder, run the `npm install` command.

## Run `npm run husky-prepare`

In the same folder, run this to install pre-commit hooks. You can
test them by editing the 'pre-commit' file in the '.husky' folder
and adding 'exit 1' at the end of the file.

## (recommended) Add prettier plugin

Add the prettier plugin, then go to `file > settings > Languages and Frameworks > Javascript > Prettier`.
Add the prettier package (it should be automatically detected), and
tick the `On 'Reformat Code' action` and the `On save` options.

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm run lint`

Runs eslint on all project's files.

### `npm run prettier`

Runs prettier on all project's files.

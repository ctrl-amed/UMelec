module.exports = {
  env: {
    es6: true,
    node: true,
  },
  parserOptions: {
    "ecmaVersion": 2018,
  },
  extends: [
    "eslint:recommended",
    "google",
  ],
  rules: {
    "no-restricted-globals": ["error", "name", "length"],
    "prefer-arrow-callback": "error",
    "quotes": ["error", "double", {"allowTemplateLiterals": true}],
    "max-len": ["error", {"code": 120, "ignoreUrls": true, "ignoreStrings": true, "ignoreTemplateLiterals": true}],
    "no-trailing-spaces": "off",
    "indent": "off",
    "comma-dangle": "off",
    "prefer-const": "warn",
    "no-unused-vars": ["warn", {"argsIgnorePattern": "^_"}],
    "linebreak-style": "off",
  },
  overrides: [
    {
      files: ["**/*.spec.*"],
      env: {
        mocha: true,
      },
      rules: {},
    },
  ],
  globals: {},
};

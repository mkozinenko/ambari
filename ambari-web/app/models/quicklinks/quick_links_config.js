var App = require('app');

App.QuickLinksConfig = DS.Model.extend({
    //general information
    name: DS.attr('string'),
    description: DS.attr('string'),
    fileName: DS.attr('string'),
    serviceName: DS.attr('string'),
    stackName: DS.attr('string'),
    stackVersion: DS.attr('string'),
    //protocol configurations
    protocol: DS.attr('object'),
    //links configurations
    links: DS.attr('array')
});

App.QuickLinksConfig.FIXTURES = [];
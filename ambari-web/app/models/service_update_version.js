var App = require('app');

App.ServiceUpdateVersion = DS.Model.extend({
    //general information
    name: DS.attr('string'),
    version: DS.attr('string'),
    description: DS.attr('string'),
    updatable: DS.attr('boolean')
});

App.ServiceUpdateVersion.FIXTURES = [];

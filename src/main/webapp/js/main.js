
var App = new Ext.App({});

var groupProxy = new Ext.data.HttpProxy({
    api: {
        read: 'usergroup/list',
    }
});

var userProxy = new Ext.data.HttpProxy({
    api: {
        read    : 'user/list',
        create  : 'user/create',
        update  : 'user/update',
        destroy : 'user/destroy'
    }
});

var groupReader = new Ext.data.JsonReader(
    {
        totalProperty: 'total',
        successProperty: 'success',
        idProperty: 'id',
        root: 'data',
        messageProperty: 'message'
    }, [
        {name: 'id', type: 'int'},
        {name: 'parentId', type: 'int', allowBlank: false},
        {name: 'name', type: 'string', allowBlank: false},
        {name: 'description', type: 'string', allowBlank: true}
    ]
);

var userReader = new Ext.data.JsonReader(
    {
        totalProperty: 'total',
        successProperty: 'success',
        idProperty: 'id',
        root: 'data',
        messageProperty: 'message'  // <-- New "messageProperty" meta-data
    }, [
        {name: 'id', type: 'int'},
        {name: 'groupId', type: 'int', allowBlank: false},
        {name: 'login', type: 'string', allowBlank: false},
        {name: 'email', type: 'string', allowBlank: true},
        {name: 'dateRegister', type:'date', dateFormat: 'time', allowBlank: false},
        {name: 'lastActivity', type:'date', dateFormat: 'time', allowBlank: true},
        {name: 'lastLogin', type:'date', dateFormat: 'time', allowBlank: true}
    ]
);

var userWriter = new Ext.data.JsonWriter({
    encode: false,
    writeAllFields: false
});

var groupStore = new Ext.data.Store({
    id: 'group-store',
    proxy: groupProxy,
    reader: groupReader
});

var userStore = new Ext.data.Store({
    id: 'user-store',
    proxy: userProxy,
    reader: userReader,
    writer: userWriter,
    autoSave: true
});

Ext.data.DataProxy.addListener('exception', function(proxy, type, action, options, res) {
    if (type === 'remote') {
        Ext.Msg.show({
            title: 'REMOTE EXCEPTION',
            msg: res.raw.reason[0].message,
            icon: Ext.MessageBox.ERROR,
            buttons: Ext.Msg.OK
        });
    }
});

// A new generic text field
var textField =  new Ext.form.TextField();

// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
var userColumns =  [{
    header: "Login",
    width: 100,
    sortable: true,
    dataIndex: 'login',
    editor: textField
},{
      header: "E-mail",
      width: 100,
      sortable: true,
      dataIndex: 'email',
      editor: textField
},{
    header: "Group",
    width: 100,
    sortable: true,
    dataIndex: 'groupId',
    editor: textField,
    renderer: function(value, metaData, record, rowIndex, colIndex, store) {
       var group = groupStore.getById(value);
       if (group) {
           return group.get("name");
       }
       return 'group#' + value;
    },
    editor: new Ext.form.ComboBox({
        displayField: 'name',
        editable: false,
        forceSelection: true,
        mode: 'local',
        store: groupStore,
        triggerAction: 'all',
        valueField: 'id'
    }),
},{
    header: "Date register",
    width: 150,
    sortable: true,
    dataIndex: 'dateRegister',
    editor: textField,
    renderer: Ext.util.Format.dateRenderer('d M Y h:i:s')
},{
    header: "Last login",
    width: 150,
    sortable: true,
    dataIndex: 'lastLogin',
    editor: textField,
    renderer: Ext.util.Format.dateRenderer('d M Y h:i:s')
},{
    header: "Last activity",
    width: 150,
    sortable: true,
    dataIndex: 'lastActivity',
    editor: textField,
    renderer: Ext.util.Format.dateRenderer('d M Y h:i:s')
},{
    xtype: 'actioncolumn',
    width: 50,
    items: [{
        icon   : 'icons/16x16/remove.png',
        tooltip: 'Remove user',
        handler: function(grid, rowIndex, colIndex) {
            var rec = userStore.getAt(rowIndex);
            if (rec.get("id") <= 0) {
                Ext.Msg.alert("Failed to remove record", "System users removing not allowed");
            } else {
                Ext.Msg.confirm(
                    'Remove user "' + rec.get('login') + '"',
                    'Do you want to remove user "' + rec.get('login') + '" with id#' + rec.get('id') + '?',
                    function (answer) {
                        if (answer === 'yes') {
                            userStore.remove(rec);
                        }
                    }
                );
            }
        }
    }]
}];

Ext.onReady(function() {
    Ext.QuickTips.init();

    groupStore.load();
    userStore.load();

    var minId = -200;

    var insertForm = new Ext.form.FormPanel({
        baseCls: 'x-plain',
        labelWidth: 55,
        defaultType: 'textfield',
        deferredRender: false,
        border: false,

        items: [{
            fieldLabel: 'Login',
            name: 'login',
            anchor:'100%',
            allowBlank: false
        },{
            fieldLabel: 'E-mail',
            name: 'email',
            anchor: '100%',
            allowBlank: false
        },{
            fieldLabel: 'Password',
            name: 'password',
            anchor: '100%'
        },{
            xtype: 'combo',
            fieldLabel: 'Group',
            name: 'groupId',
            displayField: 'name',
            editable: false,
            forceSelection: true,
            mode: 'local',
            store: groupStore,
            triggerAction: 'all',
            valueField: 'id',
            anchor: '100%',
            allowBlank: false
        }]
    });

    var insertWindow = new Ext.Window({
        layout: 'fit',
        width: 500,
        height: 200,
        minWidth: 300,
        minHeight: 200,
        closeAction: 'hide',
        plain: true,
        bodyStyle:'padding:5px;',
        items: [
            insertForm
        ],
        buttonAlign:'center',
        buttons: [{
            text: 'Save',
            handler: function () {
                if (insertForm.getForm().isValid()) {
                    userGrid.stopEditing();
                    insertWindow.hide();
                    var values = insertForm.getForm().getValues();
                    var index = groupStore.findBy(function (record) { return record.get("name") === values.groupId; });
                    var group = groupStore.getAt(index);
                    values.groupId = group.get("id");

                    Ext.Ajax.request({
                        url: 'user/create',
                        method: 'POST',
                        success: function () {
                            userStore.reload();
                        },
                        failure: function () {
                        },
                        jsonData: JSON.stringify(values)
                    });

                    insertForm.getForm().reset();
                }
            }
        },{
            text: 'Cancel',
            handler: function () {
                insertWindow.hide();
                insertForm.getForm().reset();
            }
        }]
    });

    // create user.Grid instance (@see UserGrid.js)
    var userGrid = new App.user.CGrid({
        renderTo: 'user-grid',
        store: userStore,
        columns : userColumns,
        tbar: [{
            text: 'Add',
            iconCls: 'add',
            handler: function () {
                insertWindow.show();
            }
        }]
    });
});
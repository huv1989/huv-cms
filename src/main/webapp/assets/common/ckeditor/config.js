/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	 config.width = 1000;
	 config.height = 300;
	config.toolbar = 'Full';
	config.image_previewText=' '; //
	config.filebrowserImageUploadUrl= "news/imgUpload"; //上传action
	config.toolbar_Full = [

	                       ['Source','-','NewPage','Preview','-','Templates'],

	                       ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print', 'SpellChecker', 'Scayt'],

	                       ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],

	                       ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'],

	                       '/',

	                       ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],

	                        ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],

	                        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],

	                        ['Link','Unlink','Anchor'],

	                       ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],

	                       '/',

	                        ['Styles','Format','Font','FontSize'],

	                        ['TextColor','BGColor']

	                    ];
	config.toolbar_Basic =
		[
		        ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink','-','About']
		];
};

export default function (){
    return {
        value:'',
        language:"typescript",
        // theme: 'vs-dark',
        theme: 'vs-lite',
        autoClosingBrackets: 'always',
        autoClosingDelete: 'always',
        autoClosingQuotes: 'always',
        overviewRulerBorder: true,
        smoothScrolling: true,
        automaticLayout: true,
        autoIndent: 'brackets',
        contextmenu: true,
        cursorSmoothCaretAnimation: true,
        acceptSuggestionOnEnter: 'on',
        cursorBlinking: 'smooth',
        formatOnPaste: true,
        formatOnType: true,
        fontLigatures: true,
        insertSpaces: true,
        fontSize: 16,
        suggest: {
            preview: true,
            showInlineDetails: true,
            showStatusBar: true,
            showWords: true,
            snippetsPreventQuickSuggestions: true,
            showReferences: true,
            showProperties: true,
            showSnippets:true,
            previewMode: 'subwordSmart',
            shareSuggestSelections: true
        }
    }
}
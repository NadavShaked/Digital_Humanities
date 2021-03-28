import xml.etree.ElementTree as et
import xml.etree.cElementTree as ET


def main():
    originXmlTree = et.parse("digitalHumanities.xml")
    originRoot = originXmlTree.getroot()
    fixesXmlTree = et.parse("digitalHumanitiesFix.xml")
    fixesRoot = fixesXmlTree.getroot()

    for fix in fixesRoot.find('fixes'):
        fixClause = fix.find('fixClause').text

        fixChangeSymbol = fix.find('changeSymbol')
        if fixChangeSymbol is not None:
            for mainClause in originRoot.find('clauses'):
                if mainClause.find('clauseSymbol').text == fixClause:
                    mainClause.find('clauseSymbol').text = fixChangeSymbol.text
                    break

        fixAddMainClauseBefore = fix.find('addMainClauseBefore')
        if fixAddMainClauseBefore is not None:
            fixAddMainClauseBefore = fixAddMainClauseBefore.find('mainClause')
            originRoot.find('clauses').insert(int(fixClause) - 1, fixAddMainClauseBefore)

        fixAddTextToEndOfClause = fix.find('addTextToEndOfClause')
        if fixAddTextToEndOfClause is not None:
            for category in originRoot.find('clauses'):
                if category.find('clauseSymbol').text == fixClause:
                    category.find('text').text += ' ' + fixAddTextToEndOfClause.text
                    break

    fixRecords = fixesRoot.find('metaData')
    fixRecords = fixRecords.find('records')
    originRecords = originRoot.find('metaData')
    originRecords = originRecords.find('records')
    for record in fixRecords:
        originRecords.append(record)

    tree = ET.ElementTree(originRoot)
    tree.write('fixedRule.xml', 'utf-8')


if __name__ == "__main__":
    main()

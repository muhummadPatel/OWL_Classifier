OVERVIEW:

This archive contains a collection of large OWL EL ontologies that
were used in experiments with ELK reasoner [1]. The ontologies are
expressed in OWL 2 functional-style syntax [2], and can be used with
tools such as OWL API [3] and Protege [4]. Not all of these ontologies
are strictly compliant with current OWL 2 EL specification: galen7.owl
and galen8.owl contain cyclic role chain axioms. Nevertheless, these
ontologies can be handled by specialized EL reasoners such as CEL [5],
ELK [1], jCEL [6], and Snorocket [7].
 
CONTENTS:

 README.txt               
          this file

 anatomy2012EL-obfuscated.owl
           An experimental version of the SNOMED CT anatomy part
           remodeled using role composition axioms and reflexivity
           axiom for partonomy relation. To enable unrestricted
           access, the terms in this ontology have been replaced by
           random identifiers. Related versions of this ontology can
           be found at the IHTSDO Anatomy Model Project Home [9].

 chebi.owl
          The ontology on Chemical Entities of Biological Interest
          obtained from the Ontobee website [9].

 emap.owl
          The ontology of the e-Mouse Atlas Project obtained from the
          Ontobee website [9].

 fly_anatomy.owl
          A structured controlled vocabulary of the anatomy of
          Drosophila melanogaster obtained from the Ontobee website
          [9].

 fma.owl
          The foundational model of anatomy obtained from the Ontobee
          website [9]. 

 galen.owl
          A version of the OpenGALEN ontology created by the co-ode
          project [10] reduced to OWL EL by removing
          InverseObjectProperties and FunctionalObjectProperties
          axioms. This version can also be obtained from the CEL
          reasoner website [13].

 galen7.owl
         Version 7 of the OpenGALEN ontology [11] reduced to OWL EL by
         removing InverseObjectProperties and
         FunctionalObjectProperties axioms. 

 galen8.owl
         Version 8 of the OpenGALEN ontology [11] reduced to OWL EL by
         removing InverseObjectProperties and
         FunctionalObjectProperties axioms. 

 go1.owl
        An owl version of Gene Ontology [12] obtained from a version
        published in 2006. This version has been commonly used for
        benchmarking of OWL reasoners. This version can also be
        obtained from the CEL reasoner website [13].

 go2.owl 
        A more version of Gene Ontology [12] published in March 2012.
        Obtained from the Ontobee website [9].
       
molecule_role.owl
        The Molecule role (INOH Protein name/family name ontology)
        obtained from the OBO foundry website [14].        

union.owl
        The union of the ontologies chebi.owl, emap.owl,
        fly_anatomy.owl, fma.owl, and molecule_role.owl.

COPYRIGHT:
        see individual ontology files for copyright notices, if any

[1] http://elk.semanticweb.org
[2] http://www.w3.org/TR/owl2-syntax/
[3] http://owlapi.sourceforge.net/
[4] http://protege.stanford.edu/
[5] http://lat.inf.tu-dresden.de/systems/cel/
[6] http://jcel.sourceforge.net
[7] http://research.ict.csiro.au/software/snorocket
[8] https://csfe.aceworkspace.net/sf/projects/anatomy_model_project/
[9] http://www.ontobee.org
[10] http://www.co-ode.org/galen/
[11] http://www.opengalen.org/sources/sources.html
[12] http://www.geneontology.org/GO.downloads.ontology.shtml
[13] http://lat.inf.tu-dresden.de/~meng/toyont.html
[14] http://www.obofoundry.org

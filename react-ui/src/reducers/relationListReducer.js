// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

/**
 * This reducer is in charge of setting the relationList field of the state based on
 * the particular dispatched action. This information is used in the SchemaDropdown
 * component to visualize the relation list and its associated tables.
 *
 * @author Camilo Ortiz
 */

const initialRelationListState = {
  relationList: [],
  isFetchingRelations: false,
  isErrorRelations: false
}

const relationListReducer = (state = initialRelationListState, action) => {
  switch(action.type){
    case 'FETCHING_RELATION':
      return{...state, relationList: [], isFetchingRelations: true, isErrorRelations: false};

    case 'RESOLVED_RELATION':
      return{...state, relationList: action.relationList, isFetchingRelations: false, isErrorRelations: false};

    case 'ERROR_RELATION':
      return{...state, relationList: [], isFetchingRelations: false, isErrorRelations:true};

    default:
      return state;
  }
}

export default relationListReducer;

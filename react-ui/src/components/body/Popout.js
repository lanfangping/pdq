// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

import React from 'react';

/**
  Popout is not fully implemented yet, so parts of it are commented out to avoid
  warnings.

  @author Camilo Ortiz
**/

// import Popout from 'react-popout';
// import { FaShareSquare } from 'react-icons/fa';
// import { Button } from 'reactstrap';

//Used to copy the css from the parent

// function copyStyles(sourceDoc, targetDoc) {
//   Array.from(sourceDoc.styleSheets).forEach(styleSheet => {
//     if (styleSheet.cssRules) { // for <style> elements
//       const newStyleEl = sourceDoc.createElement('style');
//
//       Array.from(styleSheet.cssRules).forEach(cssRule => {
//         // write the text of each rule into the body of the style element
//         newStyleEl.appendChild(sourceDoc.createTextNode(cssRule.cssText));
//       });
//
//       targetDoc.head.appendChild(newStyleEl);
//     } else if (styleSheet.href) { // for <link> elements loading CSS from a URL
//       const newLinkEl = sourceDoc.createElement('link');
//
//       newLinkEl.rel = 'stylesheet';
//       newLinkEl.href = styleSheet.href;
//       targetDoc.head.appendChild(newLinkEl);
//     }
//   });
// }

export default class PopoutWindow extends React.Component{
  constructor(props) {
    super(props);
    this.popout = this.popout.bind(this);
    this.popoutClosed = this.popoutClosed.bind(this);
    this.state = { isPoppedOut: false };
  }

  popout() {
    this.setState({isPoppedOut: true});
  }

  popoutClosed() {
    this.setState({isPoppedOut: false});
  }

  render() {

    return (
      null
    )

    // Popout not fully implemented yet. It depends on it's parent component being
    // mounted, so if the modal closes, so does the popout. (Not what we want)

    // if (this.state.isPoppedOut) {
    //   return (
    //     <div>
    //       <Popout
    //         title={this.props.title}
    //         options={this.props.options}
    //         >
    //         {popoutWindow => {
    //           copyStyles(window.document, popoutWindow.document);
    //           return(
    //             <div>{this.props.content}</div>
    //           );
    //         }}
    //       </Popout>
    //
    //       <Button
    //         onClick={this.popout}
    //         color="link"
    //         style={{float:'right'}}>
    //         <FaShareSquare/>
    //       </Button>
    //     </div>
    //   );
    // } else {
    //   return (
    //     <Button
    //       onClick={this.popout}
    //       color="link"
    //       style={{float:'right'}}
    //       >
    //       <FaShareSquare/>
    //     </Button>
    //   );
    // }
  }
}
